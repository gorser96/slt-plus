param(
    [Parameter(Mandatory = $true)][string]$Serial,
    [string]$Adb = "$env:LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe"
)
$ErrorActionPreference = 'Stop'
# An explicit device is required; only the permission-protected probe process is killed.
if ($Serial -notmatch '^[a-zA-Z0-9._:-]+$') { throw 'Invalid test device serial.' }
$component = 'com.logoped_plus/com.logoped_plus.data.repository.TransactionProbeService'
function Invoke-Adb([string[]]$Arguments) {
    $result = & $Adb -s $Serial @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) { throw ($result -join "`n") }
    return ($result -join "`n")
}
function Wait-Report([string]$Run, [string]$Status) {
    $deadline = [DateTime]::UtcNow.AddSeconds(20)
    while ([DateTime]::UtcNow -lt $deadline) {
        $raw = & $Adb -s $Serial shell run-as com.logoped_plus cat "files/probe-$Run.json" 2>$null
        if ($LASTEXITCODE -eq 0) {
            try { $report = ($raw -join "`n") | ConvertFrom-Json } catch { $report = $null }
            if ($report -and $report.run -eq $Run) {
                if ($report.status -eq 'ERROR') { throw "Probe failed: $($report.error)" }
                if ($report.status -eq $Status) { return $report }
            }
        }
        Start-Sleep -Milliseconds 250
    }
    throw "Timeout waiting for $Run / $Status"
}
$results = @()
Invoke-Adb @('shell','am','start','-W','-n','com.logoped_plus/.MainActivity') | Out-Null
foreach ($mode in @('BEFORE_COMMIT','AFTER_COMMIT')) {
    foreach ($rename in @($false, $true)) {
        $run = [Guid]::NewGuid().ToString('N')
        Invoke-Adb @('shell','am','startservice','-n',$component,'--es','run',$run,'--es','mode',$mode,'--ez','rename',$rename.ToString().ToLowerInvariant()) | Out-Null
        $signal = Wait-Report $run $mode
        $actualPid = (Invoke-Adb @('shell','pidof','com.logoped_plus:transactionProbe')).Trim()
        if ($actualPid -ne [string]$signal.pid) { throw 'Probe PID mismatch; refusing interruption.' }
        Invoke-Adb @('shell','am','startservice','-n',$component,'--es','action','kill') | Out-Null
        $deadline = [DateTime]::UtcNow.AddSeconds(10)
        do {
            Start-Sleep -Milliseconds 250
            $remaining = & $Adb -s $Serial shell pidof com.logoped_plus:transactionProbe 2>$null
            if ([DateTime]::UtcNow -gt $deadline) { throw 'Probe process did not stop.' }
        } while ($remaining)
        Invoke-Adb @('shell','am','startservice','-n',$component,'--es','run',$run,'--es','action','inspect') | Out-Null
        $inspection = Wait-Report $run 'INSPECTED'
        $control = @($inspection.rows | Where-Object id -eq 'control')
        $subject = @($inspection.rows | Where-Object id -eq 'subject')
        if ($control.Count -ne 1 -or $control[0].name -ne 'Контроль') { throw 'Committed control record was lost.' }
        $expectSubject = $rename -or $mode -eq 'AFTER_COMMIT'
        $expectedCount = if ($expectSubject) { 1 } else { 0 }
        if ($subject.Count -ne $expectedCount) { throw 'Unexpected subject count.' }
        if ($expectSubject) {
            $expectedName = if ($mode -eq 'BEFORE_COMMIT') { 'Старое' } else { 'Новое' }
            if ($subject[0].name -ne $expectedName) { throw 'Unexpected recovered name.' }
        }
        if (@($inspection.rows).Count -ne (1 + $expectedCount)) { throw 'Unexpected extra records.' }
        $results += [pscustomobject]@{ run=$run; mode=$mode; rename=$rename; outcome='PASS'; rows=$inspection.rows }
        Invoke-Adb @('shell','am','startservice','-n',$component,'--es','action','kill') | Out-Null
        Start-Sleep -Milliseconds 500
    }
}
$results | ConvertTo-Json -Depth 5
