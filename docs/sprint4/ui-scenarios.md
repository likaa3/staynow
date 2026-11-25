# UI-сценарії Спринту 4 — автоматизація кейсів спринта 3

Виконавець: Чаплюк Д. (ІТ / SDET) · Статус: **чернетка для узгодження з Бєлянцевою А. (ТА)**
(задача 3.3 — перелік UI-сценарів; фінальний перелік і матриця — за нею).
Demo: https://automationintesting.online (продукт «Шейді Медоуз B&B», restful-booker-platform v2.2).

## 1. Автоматизовані сценарії (11, усі — реальні TC-ID спринта 3)

| TC-ID | Вимога | Тест-метод (клас) | Ключові кроки | Локатори | Очікування / перевірки |
|---|---|---|---|---|---|
| TC-001 | F-10 | `StayNowHomePageTest#homePageShowsHotelNameBookNowAndMenu` | open `/` → огляд hero і меню | `.hero h1`, `.hero a.btn`, `.navbar-nav .nav-link` | назва «Welcome to Shady Meadows B&B»; «Book Now»; пункти Rooms, Booking, Amenities, Location, Contact, Admin — саме в цьому порядку |
| TC-002 | F-10 | `StayNowHomePageTest#menuAnchorsAndAdminOpenLoginPage` | огляд href меню → клік «Admin» | `.navbar-nav a[href='/admin']`, `#username`, `.card-header h2` | Rooms/Booking/Contact — якорі `/#…`; Admin → `/admin`, заголовок «Login», форма входу показана |
| TC-003 | F-20 | `StayNowHomePageTest#ourRoomsSectionShowsRoomCards` | прокрутка до «Our Rooms», огляд карток | `.room-card`, `.room-card .card-title`, `.card-footer .fw-bold`, `a[href*='reservation']` | 3 картки: Single, Double, Suite; ціна у форматі «£N per night»; у кожній картці кнопка «Book now» |
| TC-004 | F-30 | `StayNowHomePageTest#checkAvailabilityUpdatesRoomLinks` | дати від `LocalDate.now()` (+30/+32 днів) → «Check Availability» | `#booking .react-datepicker__input-container input`, `#booking button.btn-primary`, `.room-card a[href*='reservation']` | перелік оновлено; посилання карток ведуть на `/reservation/{id}` з `checkin`/`checkout` = обрані дати (РРРР-ММ-ДД) |
| TC-020 | F-70, GUI-85 | `StayNowHomePageTest#contactFormWithValidDataShowsThanks` | заповнити форму → «Submit» | `#name` (data-testid ContactName), `#email`, `#phone`, `#subject`, `#description`, `#contact button.btn-primary` | подяка містить «Thanks for getting in touch Test User!» і тему; форма замінена подякою |
| TC-021 | F-70 | `StayNowHomePageTest#shortSubjectIsRejected` | Subject «Test» (4 символи, a−1) → «Submit» | як TC-020 | повідомлення не надіслано: подяки немає (негативне явне очікування 5 с), форма лишається |
| TC-022 | F-70 | `StayNowHomePageTest#shortMessageIsRejected` | Message рівно 19 символів (a−1) → «Submit» | як TC-020 | повідомлення не надіслано: подяки немає, форма лишається |
| TC-023 | F-80 | `StayNowAdminAuthTest#adminLoginOpensDashboard` | `/admin` → admin/password → «Login» | `#username`, `#password`, `#doLogin` | дашборд адміністратора: URL `/admin/…`, розділи Rooms, Report, Branding, Messages |
| TC-024 | F-80 | `StayNowAdminAuthTest#wrongPasswordShowsErrorAndKeepsLoginForm` | admin/wrong_password → «Login» | `.alert.alert-danger` | «Invalid credentials»; форма входу лишається |
| TC-025 | F-85 | `StayNowAdminAuthTest#logoutTerminatesSession` | вхід → «Logout» | `button.btn-outline-danger` | вихід виконано: повернення на головну demo; повторний `/admin` показує форму входу (сесію завершено) |
| TC-069 | GUI-70 | `StayNowAdminAuthTest#adminLoginPageHeaderShowsFrontPageAndLogout` | `/admin` → огляд шапки | `#frontPageLink`, `button.btn-outline-danger` | посилання «Front Page», кнопка «Logout», заголовок «Login» |

## 2. Технічні особливості demo (виявлені при автоматизації)

| Особливість | Рішення у тестах |
|---|---|
| Кастомний програмний скрол сторінки (`window.scrollY` завжди 0) — звичайний клік по елементах нижче вікна перегляду перехоплюється | `StayNowHomePage.clickBelowFold`: `scrollIntoView` + JS-клік; для елементів у вікні (навбар, форма входу) — звичайний `click()` |
| Поля дат віджета — react-datepicker (текст, формат `dd/MM/yyyy`) | значення замінюються `Ctrl+A` + набір дати (`checkAvailability`); без `Ctrl+A` текст дописується у кінець |
| SPA (Next.js) рендериться після завантаження | усі дії захищені явними очікуваннями `WebDriverWait` + `ExpectedConditions`/лямбди; `Thread.sleep` і `implicitlyWait` не використовуються |
| Дати в тестах | рахуються від `LocalDate.now()` (BL-10 забороняє заїзд у минулому) — тест не «застаріває» |

## 3. Відкладені кейси (дефект demo, не тестів)

Потік бронювання `/reservation/{id}` (кейси **TC-007, TC-008, TC-009, TC-010, TC-011, TC-012,
TC-013, TC-014, TC-015, TC-016, TC-017, TC-018, TC-019**) станом на 23.11.2026 недоступний:

- `GET https://automationintesting.online/api/room/{id}` → **HTTP 500** (Internal Server Error)
  для всіх номерів (1, 2, 3);
- сторінка номера не рендериться — вічно показує «Loading…», форма бронювання не з'являється;
- зафіксовано діагностикою браузера (console: `Failed to load resource: the server responded
  with a status of 500`; `SyntaxError: "Internal S…" is not valid JSON`).

Це дефект demo-застосунку (оформлюється у трекері команди з міткою demo, як у спринті 3).
Кейси автоматизуються одразу після відновлення demo — локатори і схема тестів готові
(Page Object доповнюється класом сторінки номера за розміткою, знятою під час діагностики).

## 4. Drift demo відносно спринта 3

| Кейс | ОР спринта 3 | Фактична поведінка demo v2.2 | Як адаптовано |
|---|---|---|---|
| TC-021, TC-022 | «над кнопкою Submit перелік помилок, де названо поле і причину» | перелік помилок не рендериться | перевіряється частина ОР «повідомлення не надіслано: подяки немає, форма лишається» (негативне явне очікування подяки) |
| TC-025 | «після виходу — сторінка входу адміністратора» | після Logout — перенаправлення на головну сторінку demo | перевіряються: повернення на головну + втрата доступу (`/admin` знову показує форму входу) |
