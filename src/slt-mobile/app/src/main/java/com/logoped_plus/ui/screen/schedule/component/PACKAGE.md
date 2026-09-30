# com.logoped_plus.ui.screen.schedule.component

Выбор даты, участников и работа с внешними видео. [Общий указатель](../../../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [LessonDateTimeFields.kt](LessonDateTimeFields.kt) | Дата, raw часы/минуты, диапазоны 0–23/0–59 и enabled |
| [LessonDateField.kt](LessonDateField.kt) | DatePickerDialog; календарная дата через UTC, блокировка открытого диалога |
| [ChildMultiSelectField.kt](ChildMultiSelectField.kt) | Множественный выбор ID с поиском; enabled и guards открытого sheet |
| [VideoAttachmentsEditor.kt](VideoAttachmentsEditor.kt) | OpenMultipleDocuments, постоянный grant, уникальные URI в порядке выбора, подтверждение открепления, кнопка автоподкрепления с запросом `READ_MEDIA_VIDEO`, enabled/sessionId, scanInProgress/scanMessage |
| [VideoAttachmentLink.kt](VideoAttachmentLink.kt) | Имя и проверка дескриптора на IO, закрытие перед ACTION_VIEW, ошибки файла/доступа/URI/проигрывателя |

Потребители — [формы занятия](../PACKAGE.md). Черновик принадлежит ViewModel. Открепление меняет связь после сохранения, не удаляет файл и не отзывает grant. Поздние результаты picker при disabled, другой сессии или пересоздании UI игнорируются. Перед запуском проигрывателя enabled проверяется повторно. Реальный постоянный grant и открытие после нового процесса требуют отдельной приёмки.

Автоподкрепление: кнопка «Найти видео занятия» (только когда передан `onAutoAttach`, т.е. в деталях и редактировании) сначала проверяет `READ_MEDIA_VIDEO`; при отсутствии запрашивает через `RequestPermission` с тем же сессионным guard, что у пикера. Выдача — запуск поиска, отказ — локальное сообщение об ошибке, повторное нажатие повторяет запрос. Во время скана кнопка неактивна (`scanInProgress`), итоги — `scanMessage`. Ссылки MediaStore открываются через `VideoAttachmentLink`, пока действует разрешение на медиа; при отзыве разрешения — стандартная ошибка недоступного файла, открепление остаётся.

## Проверки

- [VideoAttachmentsTest.kt](../../../../../../../../androidTest/java/com/logoped_plus/ui/screen/schedule/component/VideoAttachmentsTest.kt) — androidTest.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../../../openspec/verification/persist-lessons/validation.md).
