---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-001
**Title:** Зависання (нескінченне завантаження) сторінки номера та форми бронювання при переході за кнопкою «Book now» (F-50, F-60)
**Severity:** Critical | **Priority:** P2
**Linked:** test case TC-007, requirement F-50, F-60
**Reviewer:** Гетьман Є., 2026-11-11

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
**Title:** Відсутність кнопки повернення до каталогу після успішного відправлення контактної форми (GUI-85)
**Severity:** Trivial | **Priority:** P3
**Linked:** test case TC-020, requirement GUI-85
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Після успішного надсилання повідомлення через контактну форму користувачу відображається блок подяки, але в ньому немає кнопки або посилання для повернення до початкового стану форми чи каталогу. Severity Trivial: це суто косметичний недолік інтерфейсу, саме повідомлення доставлено успішно. Priority P3: дефект не блокує виконання жодних інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. На головній сторінці натиснути пункт меню «Contact» (перехід до розділу «Send Us a Message»).
3. Заповнити поля валідними даними: Name — `Test User`, Email — `tc.tester@example.com`, Phone — `01234567890`, Subject — `TC020 subject`, Message — `Message text for contact form check.`.
4. Натиснути кнопку «Submit».

### Actual result
Після кроку 4 замість форми з'являється повідомлення «Thanks for getting in touch Test User!», але під текстом відсутній елемент навігації (кнопка повернення чи оновлення форми).

### Expected result
Після кроку 4 під текстом підтвердження відображається кнопка або посилання (наприклад, «Back to Home» або «Send another message») згідно з вимогою GUI-85.

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-002-1.png — Блок подяки після відправлення контактної форми без кнопки повернення.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-003
**Title:** Відсутність червоного підсвічування полів вводу при помилці авторизації в адмінпанелі (GUI-70)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-024, requirement F-80, GUI-70
**Reviewer:** Гетьман Є., 2026-11-11

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
**Severity:** Major | **Priority:** P2
**Linked:** test case TC-026, requirement F-90
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Після успішного додавання нового номера (`R026`) через панель адміністратора цей номер не з'являється у публічному списку «Our Rooms» на клієнтській сторінці. Severity Major: порушено важливу функціональність публікації створених номерів у клієнтському каталозі. Priority P2: дефект унеможливлює перевірку клієнтських сценаріїв для новостворених номерів.

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
**Reviewer:** Гетьман Є., 2026-11-11

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
**Title:** Поле «Phone» у контактній формі приймає літерні символи замість цифр (F-70)
**Severity:** Normal | **Priority:** P3
**Linked:** test case TC-021, requirement F-70
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Контактна форма дозволяє надіслати повідомлення, у якому в полі номера телефону «Phone» введено виключно літери латинського алфавіту (`abcdefghijk`). Severity Normal: відсутність перевірки формату призводить до збереження некоректних контактних даних, через що адміністратор не зможе зателефонувати клієнту, але система загалом працює. Priority P3: дефект не блокує інші перевірки.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. Прокрутити сторінку до контактної форми «Send Us a Message».
3. Заповнити поля: Name — `Test`, Email — `Test@example.com`, Subject — `Booking question`, Message — `Hello, please call me back regarding my reservation.`.
4. У поле «Phone» ввести 11 літер: `abcdefghijk`.
5. Натиснути кнопку «Submit».

### Actual result
Після кроку 5 форма успішно відправляється, з'являється повідомлення «Thanks for getting in touch Test!». Повідомлення про помилку формату телефону відсутнє.

### Expected result
Після кроку 5 відправлення форми блокується, над кнопкою з'являється повідомлення про помилку валідації поля «Phone» (допускаються лише цифри та символи телефонного формату згідно з F-70).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-006-1.png — Повідомлення про успішне відправлення контактної форми із введеними літерами `abcdefghijk` у полі Phone.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-007
**Title:** Поле «Name» у контактній формі приймає цифри та спеціальні символи замість імені (F-70)
**Severity:** Minor | **Priority:** P3
**Linked:** test case TC-022, requirement F-70
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Поле «Name» у формі зворотного зв'язку не перевіряє вміст на допустимі символи і дозволяє відправити рядок, що складається лише з цифр та спецсимволів (`12345!@#$%`). Severity Minor: незначний дефект валідації вхідних даних, що не порушує стабільність роботи застосунку. Priority P3: не впливає на проходження інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. Перейти до контактної форми «Send Us a Message».
3. У поле «Name» ввести `12345!@#$%`.
4. Заповнити решту полів валідними даними: Email — `Test@example.com`, Phone — `01234567890`, Subject — `Room inquiry`, Message — `Hello, I want to book a room for next week.`.
5. Натиснути кнопку «Submit».

### Actual result
Після кроку 5 форма приймає дані та виводить повідомлення «Thanks for getting in touch 12345!@#$%!».

### Expected result
Після кроку 5 система відхиляє відправлення і виводить повідомлення про некоректний формат імені у полі «Name» (F-70).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-007-1.png — Екран успішного відправлення форми з текстом «Thanks for getting in touch 12345!@#$%!».

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-008
**Title:** Поле «Email» у контактній формі приймає некоректну адресу без домену верхнього рівня `test@com` (F-70, GUI-60)
**Severity:** Normal | **Priority:** P3
**Linked:** test case TC-038, requirement F-70, GUI-60
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Валідатор поля «Email» у контактній формі пропускає електронну адресу без крапки та домену верхнього рівня (`test@com`). Severity Normal: збереження невалідного email унеможливлює зворотний зв'язок із клієнтом електронною поштою. Priority P3: дефект ізольований у межах одного поля форми і не блокує інші тести.

### Steps to reproduce
1. Відкрити https://automationintesting.online у браузері Chrome (нова сесія, інкогніто).
2. Прокрутити сторінку до форми «Send Us a Message».
3. У поле «Email» ввести `test@com`.
4. Заповнити інші поля валідними даними: Name — `Test`, Phone — `01234567890`, Subject — `General question`, Message — `Testing email validation on the contact form.`.
5. Натиснути кнопку «Submit».

### Actual result
Після кроку 5 форма успішно відправляється, з'являється блок підтвердження «Thanks for getting in touch Test!».

### Expected result
Після кроку 5 система блокує відправлення форми та виводить над кнопкою зрозуміле повідомлення про некоректний формат поля «Email» (F-70, GUI-60).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; без входу в обліковий запис

### Attachments
BUG-008-1.png — Підтвердження відправлення форми після введення невалідного email `test@com`.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-009
**Title:** Поле «Room #» приймає HTML-теги (`<h1>Test</h1>`) без санітизації при створенні номера в адмінпанелі (F-90)
**Severity:** Major | **Priority:** P3
**Linked:** test case TC-027, requirement F-90
**Reviewer:** Гетьман Є., 2026-11-11

### Description
Під час створення нового номера в адмінпанелі поле «Room #» приймає сирі HTML-теги (наприклад, `<h1>Test</h1>`) без валідації та екранування спецсимволів. Severity Major: відсутність санітизації вхідних даних у формі керування номерами створює ризик порушення верстки та потенційних XSS-ін'єкцій. Priority P3: дефект не заважає виконанню інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. Авторизуватися з обліковим записом: Username — `admin`, Password — `password`.
3. У формі додавання номера в поле «Room #» ввести `<h1>Test</h1>`.
4. Вказати Type — `Double`, Accessible — `false`, Price — `147`, позначити `WiFi` та натиснути кнопку «Create».

### Actual result
Після кроку 4 система без помилок зберігає номер, у переліку номерів адмінпанелі з'являється запис із рядком `<h1>Test</h1>`.

### Expected result
Після кроку 4 система відхиляє збереження номера з недопустимими спецсимволами `<>` у полі «Room #» та виводить повідомлення про помилку валідації (F-90).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-009-1.png — Список номерів в адмінпанелі зі створеною кімнатою `<h1>Test</h1>`.

---
name: Звіт про дефект
labels: ["status:new", "sprint:3"]
---
**Bug ID:** BUG-010
**Title:** У формі створення номера в адмінпанелі відсутнє поле для завантаження фотографії картки номера (F-90, GUI-30)
**Severity:** Normal | **Priority:** P3
**Linked:** test case TC-034, requirement F-90, GUI-30
**Reviewer:** Гетьман Є., 2026-11-11

### Description
У формі створення номера в панелі адміністратора фізично відсутнє поле для завантаження зображення або вказання URL картинки номера, хоча за вимогою GUI-30 кожна картка номера в каталозі повинна мати власне зображення. Severity Normal: адміністратор не може задати або змінити фотографію створюваного номера, але основні параметри зберігаються. Priority P3: не блокує виконання інших перевірок.

### Steps to reproduce
1. Відкрити https://automationintesting.online/admin у браузері Chrome (нова сесія, інкогніто).
2. Увійти в систему: Username — `admin`, Password — `password`.
3. Оглянути рядок створення нового номера на сторінці керування номерами «Rooms».

### Actual result
Після кроку 3 у формі наявні лише поля «Room #», «Type», «Accessible», «Price» та прапорці «Room details». Поле або кнопка для додавання фотографії номера відсутні.

### Expected result
У формі створення номера присутнє поле для завантаження файлу зображення або введення URL-адреси фотографії для відображення на картці номера (F-90, GUI-30).

### Environment
demo https://automationintesting.online, 2026-11-11; Chrome 142.0; Windows 11; обліковий запис: admin

### Attachments
BUG-010-1.png — Форма створення номера в адмінпанелі, де відсутнє поле для додавання фото.