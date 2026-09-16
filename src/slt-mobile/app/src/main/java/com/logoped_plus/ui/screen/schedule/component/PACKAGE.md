# com.logoped_plus.ui.screen.schedule.component

Выбор даты, участников и работа с внешними видео. [Общий указатель](../../../../../../../../../../PACKAGES.md).

| Файл | Назначение |
|---|---|
| [LessonDateTimeFields.kt](LessonDateTimeFields.kt) | Дата, raw часы/минуты, диапазоны 0–23/0–59 и enabled |
| [LessonDateField.kt](LessonDateField.kt) | DatePickerDialog; календарная дата через UTC, блокировка открытого диалога |
| [ChildMultiSelectField.kt](ChildMultiSelectField.kt) | Множественный выбор ID с поиском; enabled и guards открытого sheet |
| [VideoAttachmentsEditor.kt](VideoAttachmentsEditor.kt) | OpenMultipleDocuments, постоянный grant, уникальные URI в порядке выбора, подтверждение открепления, enabled/sessionId |
| [VideoAttachmentLink.kt](VideoAttachmentLink.kt) | Имя и проверка дескриптора на IO, закрытие перед ACTION_VIEW, ошибки файла/доступа/URI/проигрывателя |

Потребители — [формы занятия](../PACKAGE.md). Черновик принадлежит ViewModel. Открепление меняет связь после сохранения, не удаляет файл и не отзывает grant. Поздние результаты picker при disabled, другой сессии или пересоздании UI игнорируются. Перед запуском проигрывателя enabled проверяется повторно. Реальный постоянный grant и открытие после нового процесса требуют отдельной приёмки.

## Проверки

- [VideoAttachmentsTest.kt](../../../../../../../../androidTest/java/com/logoped_plus/ui/screen/schedule/component/VideoAttachmentsTest.kt) — androidTest.

Результаты и открытые критерии — [приёмка 010](../../../../../../../../../../openspec/verification/persist-lessons/validation.md).
