---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-001
**Title:** Зависання (нескінченне завантаження) сторінки номера та форми бронювання при переході за кнопкою «Book now» (F-50, F-60)
**Severity:** Critical | **Priority:** P2
**Linked:** test case TC-007, requirement F-50, F-60
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
При натисканні кнопки «Book now» та переході на сторінку `/reservation/{id}` з'являється нескінченний індикатор завантаження, а деталі номера, календар і форма бронювання не відображаються. Дефект має рівень Critical, оскільки повністю блокує ключову бізнес-функцію перегляду деталей і бронювання номерів. Priority P2 призначено тому, що дефект блокує 17 залежних тест-кейсів (TC-005, TC-008…TC-019, TC-035, TC-036, TC-037, TC-040), але дозволяє перевіряти інший функціонал сайту.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. У полі «Check In» ввести `03/16/2027`, у полі «Check Out» ввести `03/18/2027` та натиснути «Check Availability».
3. На картці номера «Single» натиснути кнопку «Book now».

### Actual result
Після кроку 3 відкривається адреса `/reservation/1?checkin=2027-03-16&checkout=2027-03-18`, проте замість даних номера, календаря, блоку «Price Summary» та форми бронювання відображається нескінченна анімація завантаження.

### Expected result
Після кроку 3 протягом не більше ніж 3 секунд (F-40) завантажується сторінка номера з описом, правилами, календарем, блоком «Price Summary» (F-50) та доступною формою для введення даних гостя (F-60).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-001-1.png — Сторінка бронювання з нескінченним індикатором завантаження після кроку 3.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-002
**Title:** Видалення номера в адмінпанелі завершується помилкою сервера 500: номер R027 лишається в переліку (F-90)
**Severity:** Major | **Priority:** P3
**Linked:** test case TC-027, requirement F-90
**Reviewer:** Гетьман Є., 2026-11-13
**Reporter:** Шкарупа М.
**Date:** 2026-11-13

### Description
Після створення тестового номера R027 натискання на піктограму ✖ у його рядку не видаляє номер: запит на видалення завершується помилкою сервера 500, рядок не зникає, користувач повідомлення не бачить. Вимога F-90 передбачає створення і видалення номера. Severity Major: функція видалення номера в адмінпанелі не працює, тоді як створення працює. Priority P3: дефект не блокує виконання інших тест-кейсів (TC-027 завершується як failed на кроці 5).

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. Увійти з обліковим записом адміністратора: Username — `admin`, Password — `password`.
3. У рядку створення номера заповнити поля: Room # — `R027`, Type — `Double`, Accessible — `false`, Price — `147`, позначити `WiFi`.
4. Натиснути кнопку «Create» (у переліку номерів з'являється рядок R027).
5. Відкрити DevTools (F12), вкладку «Console», потім натиснути піктограму ✖ у кінці рядка R027 і зачекати 5 секунд.

### Actual result
Після кроку 5 рядок R027 (Double, false, 147, WiFi) лишається в переліку номерів. У «Console» з'являється запис: `DELETE https://automationintesting.online/api/room/4 500 (Internal Server Error)`. На сторінці повідомлення про помилку немає.

### Expected result
Після кроку 5 запит на видалення завершується успішно, рядок R027 зникає з переліку номерів адмінпанелі (F-90: видалення номера).

### Environment
demo https://automationintesting.online, 2026-11-13; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-002-1.png — Перелік номерів з рядком R027 і «Console» із записом DELETE /api/room/4 500 (Internal Server Error) після кроку 5.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-003
**Title:** Відсутність червоного підсвічування полів вводу при помилці авторизації в адмінпанелі (GUI-70)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-024, requirement F-80, GUI-70
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Під час введення хибного пароля на сторінці входу в адмінпанель з'являється повідомлення про помилку, проте самі поля «Username» та «Password» не виділяються червоною рамкою як помилкові. Severity Minor: це незначне порушення вимог до графічного інтерфейсу (GUI-70), сама відмова у доступі (F-80) працює правильно. Priority P3: дефект не перешкоджає подальшому тестуванню системи.

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. У поле «Username» ввести `admin`.
3. У поле «Password» ввести `wrongpass1`.
4. Натиснути кнопку «Login».

### Actual result
Після кроку 4 відображається повідомлення «Invalid credentials», але контури (рамки) полів «Username» та «Password» залишаються стандартного сірого кольору.

### Expected result
Після кроку 4 доступ не надається, а рамки полів «Username» та «Password» візуально підсвічуються червоним кольором на знак помилки валідації (GUI-70).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-003-1.png — Форма входу в адмінпанель після невдалої спроби авторизації без червоного підсвічування полів.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-004
**Title:** Створений адміністратором номер R026 не відображається у каталозі на головній сторінці (F-90)
**Severity:** Major | **Priority:** P3
**Linked:** test case TC-026, requirement F-90
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Після успішного додавання нового номера (`R026`) через панель адміністратора цей номер не з'являється у публічному списку «Our Rooms» на клієнтській сторінці. Severity Major: порушено важливу функціональність публікації створених номерів у клієнтському каталозі. Priority P3: дефект не блокує жодного тест-кейса — перевірки каталогу «Our Rooms» виконуються на наявних номерах (Single, Double, Suite).

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. Увійти з обліковим записом адміністратора: Username — `admin`, Password — `password`.
3. У рядку створення номера заповнити поля: Room # — `R026`, Type — `Double`, Accessible — `true`, Price — `137`, обрати зручності `WiFi`, `TV` та натиснути кнопку «Create».
4. Натиснути «Logout», відкрити клієнтську сторінку https://automationintesting.online та переглянути розділ «Our Rooms».

### Actual result
Після кроку 3 номер `R026` присутній у списку в адмінпанелі, але після кроку 4 у розділі «Our Rooms» на головній сторінці картка номера `Double` з ціною `£137 per night` відсутня.

### Expected result
Після кроку 4 новостворений номер `R026` типу `Double` з ціною `£137 per night` та зручностями `WiFi, TV` відображається у публічному каталозі «Our Rooms» (F-90).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-004-1.png — Розділ «Our Rooms» на головній сторінці, де відсутній створений номер R026 (£137).

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-005
**Title:** Сесія адміністратора не завершується автоматично після 31 хвилини бездіяльності (F-130, S-20)
**Severity:** Major | **Priority:** P3
**Linked:** test case TC-030, requirement F-130, S-20
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Після 31 хвилини повної бездіяльності у відкритій вкладці адмінпанелі сесія адміністратора залишається активною і не вимагає повторного входу. Severity Major: це суттєве порушення вимог F-130 та S-20 щодо автоматичного завершення неактивної сесії через 30 хвилин. Priority P3: дефект не блокує виконання жодних інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. Увійти в систему: Username — `admin`, Password — `password`.
3. Залишити вкладку браузера відкритою без жодних дій користувача протягом 31 хвилини.
4. Натиснути на пункт меню «Messages» (або «Rooms») у шапці адмінпанелі.

### Actual result
Після кроку 4 сторінка адмінпанелі відкриває вибраний розділ і продовжує працювати без перенаправлення на сторінку входу.

### Expected result
Після кроку 4 сесія примусово завершується через перевищення ліміту неактивності (30 хвилин), а замість розділу відкривається сторінка входу «Login» (F-130, S-20).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-005-1.png — Активна сторінка адмінпанелі після 31 хвилини бездіяльності.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-006
**Title:** Контактна форма приймає в полі «Phone» лише літери (`abcdefghijk`)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-073, requirement F-70 (вимога не задає цей формат)
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Контактна форма надсилає повідомлення, коли в полі «Phone» введено лише літери. SRS (F-70) задає для Phone тільки довжину 11–21 символів і не описує формат; вимога мовчить, тому очікуваний результат ґрунтується на логіці: поле призначене для телефонного номера. Severity Minor: некоректний телефон унеможливлює зв'язок із гостем, але система працює. Priority P3: дефект не блокує інші тест-кейси.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. На головній сторінці натиснути пункт меню «Contact».
3. У поле «Name» ввести `Test User`.
4. У поле «Email» ввести `tc.tester@example.com`.
5. У поле «Phone» ввести `abcdefghijk`.
6. У поле «Subject» ввести `TC073 subject`.
7. У поле «Message» ввести `Message text for contact form check.`
8. Натиснути «Submit».

### Actual result
Після кроку 8 форму надіслано, показано «Thanks for getting in touch Test User!» і тему «TC073 subject». Повідомлення про помилку в полі «Phone» немає.

### Expected result
Після кроку 8 форму не надіслано; над кнопкою «Submit» показано повідомлення про некоректний телефон. Обґрунтування: F-70 формат не визначає (лише довжину 11–21); за логікою телефон складається з цифр та знаків «+», «-».

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-006-1.png — екран після кроку 8: блок подяки «Thanks for getting in touch Test User!» і тема «TC073 subject» після введення літер у полі Phone.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-007
**Title:** Контактна форма приймає в полі «Name» лише спецсимволи (`!@#$%`)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-074, requirement F-70 (вимога не задає цей формат)
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Контактна форма надсилає повідомлення, коли в полі «Name» введено лише спецсимволи. SRS (F-70) правил формату для Name не містить; вимога мовчить, тому очікування ґрунтується на логіці: ім'я не складається лише зі знаків пунктуації. Severity Minor: незначна втрата перевірки вхідних даних, робота застосунку не порушена. Priority P3: дефект не блокує інші тест-кейси.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. На головній сторінці натиснути пункт меню «Contact».
3. У поле «Name» ввести `!@#$%`.
4. У поле «Email» ввести `tc.tester@example.com`.
5. У поле «Phone» ввести `01234567890`.
6. У поле «Subject» ввести `TC074 subject`.
7. У поле «Message» ввести `Message text for contact form check.`
8. Натиснути «Submit».

### Actual result
Після кроку 8 форму надіслано, показано «Thanks for getting in touch !@#$%!» і тему «TC074 subject». Повідомлення про помилку немає.

### Expected result
Після кроку 8 форму не надіслано; над кнопкою «Submit» показано повідомлення про некоректне ім'я. Обґрунтування: F-70 формат Name не визначає; за логікою ім'я містить літери.

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-007-1.png — екран після кроку 8: блок подяки «Thanks for getting in touch !@#$%!» і тема «TC074 subject».

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-008
**Title:** Контактна форма приймає в полі «Email» адресу без домену верхнього рівня (`test@com`)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-075, requirement F-70 (вимога не задає цей формат)
**Reviewer:** Гетьман Є., 2026-11-12
**Reporter:** Шкарупа М.
**Date:** 2026-11-11

### Description
Контактна форма надсилає повідомлення з адресою `test@com`, у якій немає крапки та домену верхнього рівня. SRS (F-70, GUI-60) формат Email не визначає; вимога мовчить, тому очікування ґрунтується на логіці: для зворотного зв'язку потрібна повна адреса. Severity Minor: на таку адресу не можна відповісти, але система працює. Priority P3: дефект не блокує інші тест-кейси.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. На головній сторінці натиснути пункт меню «Contact».
3. У поле «Name» ввести `Test User`.
4. У поле «Email» ввести `test@com`.
5. У поле «Phone» ввести `01234567890`.
6. У поле «Subject» ввести `TC075 subject`.
7. У поле «Message» ввести `Message text for contact form check.`
8. Натиснути «Submit».

### Actual result
Після кроку 8 форму надіслано, показано «Thanks for getting in touch Test User!» і тему «TC075 subject». Повідомлення про помилку в полі «Email» немає.

### Expected result
Після кроку 8 форму не надіслано; над кнопкою «Submit» показано повідомлення про некоректну адресу Email. Обґрунтування: F-70 і GUI-60 формат Email не визначають; за логікою адреса має містити домен із крапкою.

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-008-1.png — екран після кроку 8: блок подяки «Thanks for getting in touch Test User!» і тема «TC075 subject».

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-009
**Title:** Видалення повідомлення в адмінпанелі завершується помилкою сервера 500: рядок «TC028 subject» лишається в переліку (F-100)
**Severity:** Major | **Priority:** P3
**Linked:** test case TC-028, requirement F-100
**Reviewer:** Гетьман Є., 2026-11-13
**Reporter:** Шкарупа М.
**Date:** 2026-11-13

### Description
Після надсилання тестового повідомлення через контактну форму адміністратор не може видалити його з переліку: запит на видалення завершується помилкою сервера 500, рядок не зникає, повідомлення про помилку користувач не бачить. Вимога F-100 передбачає видалення повідомлення. Severity Major: функція видалення повідомлень не працює, тоді як перегляд працює. Priority P3: дефект не блокує виконання інших тест-кейсів (TC-028 завершується як failed на кроці 6).

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. На головній сторінці натиснути пункт меню «Contact».
3. Заповнити форму: Name — `123123123`, Email — `tc.tester@example.com`, Phone — `01234567890`, Subject — `TC028 subject`, Message — `Message text for contact form check.`; натиснути «Submit».
4. Відкрити https://automationintesting.online/admin, увійти: Username — `admin`, Password — `password`, натиснути «Login».
5. Натиснути пункт меню «Messages».
6. Відкрити DevTools (F12), вкладку «Console», потім натиснути піктограму ✖ у кінці рядка з темою `TC028 subject` і зачекати 5 секунд.

### Actual result
Після кроку 6 рядок `TC028 subject` лишається в переліку повідомлень. У «Console» з'являється запис: `DELETE https://automationintesting.online/api/message/2 500 (Internal Server Error)`. На сторінці повідомлення про помилку немає.

### Expected result
Після кроку 6 запит на видалення завершується успішно, рядок `TC028 subject` зникає з переліку повідомлень (F-100: видалення повідомлення).

### Environment
demo https://automationintesting.online, 2026-11-13; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-009-1.png — Перелік повідомлень з рядком `TC028 subject` і «Console» із записом DELETE /api/message/2 500 (Internal Server Error) після кроку 6.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-010
**Title:** Поля «Check In» і «Check Out» показують дату у форматі дд/мм/рррр у браузері з мовою en-US замість мм/дд/рррр (GUI-20)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-032, requirement GUI-20
**Reviewer:** Гетьман Є., 2026-11-13
**Reporter:** Шкарупа М.
**Date:** 2026-11-13

### Description
У Chrome з мовою інтерфейсу English (United States) після вибору дат у календарі поля «Check In» і «Check Out» показують день перед місяцем. Вимога GUI-20 задає для локалі en-US формат мм/дд/рррр. Severity Minor: це неточність відображення дати (проблема інтерфейсу), бронювання за вибраними датами не порушене. Priority P3: дефект не блокує виконання інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome з мовою інтерфейсу English (United States) (нова сесія, інкогніто).
2. Натиснути на поле «Check In» і в календарі вибрати 15 грудня 2026 року.
3. Натиснути на поле «Check Out» і в календарі вибрати 17 грудня 2026 року.

### Actual result
Після кроку 2 у полі «Check In» показано `15/12/2026`, після кроку 3 у полі «Check Out» показано `17/12/2026` (формат дд/мм/рррр).

### Expected result
Після кроку 2 у полі «Check In» показано `12/15/2026`, після кроку 3 у полі «Check Out» показано `12/17/2026` (формат мм/дд/рррр для локалі en-US, GUI-20).

### Environment
demo https://automationintesting.online, 2026-11-13; Chrome 142.0 (мова en-US); Windows 11; без входу в обліковий запис

### Attachments
BUG-010-1.png — Поля «Check In» (15/12/2026) і «Check Out» (17/12/2026) у форматі дд/мм/рррр після кроку 3.
