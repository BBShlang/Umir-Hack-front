<template>
  <div class="page-wrapper">
    <div class="legal-page">
      <div class="legal-page__bg" aria-hidden="true">
        <div class="legal-page__bg-grid" />
        <div class="legal-page__bg-glow legal-page__bg-glow--1" />
        <div class="legal-page__bg-glow legal-page__bg-glow--2" />
      </div>

      <div class="legal-page__container">
        <div class="legal-page__card">
          <h1 class="legal-page__title">Условия использования</h1>
          <p class="legal-page__date">Последнее обновление: 4 апреля 2026 г.</p>

          <div class="legal-page__content">

            <section>
              <h2>1. Общие положения</h2>
              <p>Настоящие Условия использования (далее — «Условия») регулируют порядок предоставления и использования платформы «ДиплоРеестр» (далее — «Платформа», «Сервис»). Используя Сервис, вы подтверждаете, что ознакомлены и согласны со всеми положениями настоящих Условий.</p>
              <p>Сервис предназначен для ведения цифрового реестра дипломов, верификации документов об образовании и предоставления доступа к данным о дипломах уполномоченным лицам.</p>
            </section>

            <section>
              <h2>2. Категории пользователей</h2>
              <p>Сервис предоставляет функциональность для следующих категорий пользователей:</p>
              <ul>
                <li><strong>ВУЗ (образовательная организация):</strong> загрузка реестра дипломов, подписание записей, управление статусом (активен / отозван).</li>
                <li><strong>Студент (выпускник):</strong> просмотр своих дипломов, генерация ссылок и QR-кодов для предоставления доступа работодателям.</li>
                <li><strong>Работодатель (HR):</strong> проверка подлинности дипломов по номеру, хэшу или QR-коду, использование API для автоматизированной верификации.</li>
              </ul>
              <p>Регистрируясь на Платформе, вы подтверждаете, что относитесь к одной из указанных категорий и имеете законные основания для использования соответствующего функционала.</p>
            </section>

            <section>
              <h2>3. Регистрация и безопасность аккаунта</h2>
              <p>Для использования Сервиса необходимо создать учётную запись, предоставив достоверную информацию. Вы обязуетесь:</p>
              <ul>
                <li>Предоставлять актуальные и полные данные при регистрации.</li>
                <li>Обеспечивать конфиденциальность логина и пароля.</li>
                <li>Немедленно уведомлять администрацию Сервиса о любом случае несанкционированного доступа к вашему аккаунту.</li>
                <li>Не передавать учётные данные третьим лицам.</li>
              </ul>
              <p>Администрация оставляет за собой право заблокировать или удалить учётную запись при подозрении на несанкционированный доступ или нарушение настоящих Условий.</p>
            </section>

            <section>
              <h2>4. Правила использования</h2>
              <p>При использовании Сервиса вы обязуетесь:</p>
              <ul>
                <li>Не использовать Сервис для передачи заведомо ложных или поддельных данных о дипломах.</li>
                <li>Не предпринимать попыток несанкционированного доступа к данным других пользователей, серверам или инфраструктуре Сервиса.</li>
                <li>Не использовать автоматизированные средства (скрипты, боты, парсеры) для сбора данных, за исключением официально предоставленного API.</li>
                <li>Не распространять вредоносное программное обеспечение через Сервис.</li>
                <li>Не нарушать права третьих лиц, включая права на интеллектуальную собственность.</li>
              </ul>
              <p>ВУЗы несут ответственность за достоверность загружаемых данных реестра и корректность статусов дипломов.</p>
            </section>

            <section>
              <h2>5. Проверка дипломов и API</h2>
              <p>Работодатели и иные уполномоченные лица могут осуществлять проверку дипломов следующими способами:</p>
              <ul>
                <li><strong>По номеру диплома:</strong> ввод серийного номера в интерфейсе верификации.</li>
                <li><strong>По QR-коду:</strong> сканирование QR-кода, предоставленного владельцем диплома.</li>
                <li><strong>По временной ссылке:</strong> переход по ссылке, сгенерированной студентом (действует в течение указанного срока).</li>
                <li><strong>Через API:</strong> программная проверка через предоставленный REST API с использованием авторизационного токена.</li>
              </ul>
              <p>Результаты проверки носят информационный характер и основаны на данных, предоставленных ВУЗом. Администрация не несёт ответственности за последствия решений, принятых на основании результатов проверки.</p>
            </section>

            <section>
              <h2>6. Ограничение ответственности</h2>
              <p>Сервис предоставляется на условиях «как есть» (as is). Администрация не гарантирует:</p>
              <ul>
                <li>Бесперебойную и безошибочную работу Сервиса.</li>
                <li>Соответствие результатов проверки фактическому состоянию документов.</li>
                <li>Своевременность обновления данных в реестре.</li>
              </ul>
              <p>В максимально допустимой законом мере Администрация не несёт ответственности за любые прямые, косвенные, случайные или последующие убытки, возникшие в результате использования или невозможности использования Сервиса.</p>
              <p>Администрация оставляет за собой право в любое время изменять, приостанавливать или прекращать работу Сервиса (или его отдельных частей) без предварительного уведомления.</p>
            </section>

            <section>
              <h2>7. Интеллектуальная собственность</h2>
              <p>Все материалы, программное обеспечение, дизайн, тексты и иные элементы Сервиса являются объектами авторского права и защищены законодательством Российской Федерации об интеллектуальной собственности. Копирование, модификация или распространение материалов Сервиса без письменного согласия правообладателя запрещены.</p>
            </section>

            <section>
              <h2>8. Отзыв и аннулирование дипломов</h2>
              <p>ВУЗ вправе изменить статус диплома на «Аннулирован» в случае выявления подлога, ошибки или иного основания. Студент и работодатель, осуществлявшие проверку данного диплома, могут быть уведомлены об изменении статуса. Решение об аннулировании является окончательным и обжалованию через Сервис не подлежит.</p>
            </section>

            <section>
              <h2>9. Применимое право и разрешение споров</h2>
              <p>Настоящие Условия регулируются законодательством Российской Федерации. Все споры и разногласия, возникающие из настоящих Условий или в связи с ними, разрешаются путём переговоров. В случае невозможности урегулирования спора путём переговоров он подлежит рассмотрению в суде по месту нахождения Администрации Сервиса.</p>
            </section>

            <section>
              <h2>10. Изменение Условий</h2>
              <p>Администрация оставляет за собой право вносить изменения в настоящие Условия. Актуальная версия всегда доступна на данной странице. Дата последнего обновления указана в начале документа. Продолжая использовать Сервис после внесения изменений, вы принимаете обновлённые Условия.</p>
            </section>

            <section>
              <h2>11. Контактная информация</h2>
              <p>По вопросам, связанным с использованием Сервиса, обращайтесь:</p>
              <ul>
                <li>Email: <a href="mailto:support@diploms.ru">support@diploms.ru</a></li>
                <li>Телефон: <a href="tel:+78001234567">8 (800) 123-45-67</a></li>
              </ul>
            </section>

          </div>

          <button class="legal-page__back" @click="goBack">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Вернуться
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.back()
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; }

.legal-page {
  min-height: 100vh;
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

/* ---- Фон как hero ---- */
.legal-page__bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.legal-page__bg-grid {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.legal-page__bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.legal-page__bg-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.legal-page__bg-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

/* ---- Карточка ---- */
.legal-page__container {
  position: relative;
  z-index: 1;
}

.legal-page__card {
  max-width: 780px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-10) var(--space-8) var(--space-8);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.25);
}

.legal-page__title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
  margin: 0 0 var(--space-2);
}

.legal-page__date {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0 0 var(--space-8);
}

/* ---- Контент ---- */
.legal-page__content {
  font-size: var(--font-size-base);
  color: var(--color-black);
  line-height: 1.7;
}

.legal-page__content section {
  margin-bottom: var(--space-8);
}

.legal-page__content section:last-of-type {
  margin-bottom: 0;
}

.legal-page__content h2 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  margin: 0 0 var(--space-4);
  line-height: 1.3;
}

.legal-page__content p {
  margin: 0 0 var(--space-3);
}

.legal-page__content ul {
  margin: var(--space-2) 0 var(--space-3);
  padding-left: var(--space-6);
}

.legal-page__content li {
  margin-bottom: var(--space-2);
}

.legal-page__content a {
  color: var(--color-main-blue);
  text-decoration: none;
  font-weight: var(--font-weight-semibold);
}

.legal-page__content a:hover {
  text-decoration: underline;
}

/* ---- Назад ---- */
.legal-page__back {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  margin-top: var(--space-8);
  padding: var(--space-3) var(--space-5);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  color: #fff;
  background: var(--color-main-blue);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.legal-page__back:hover {
  background: #1a5bbd;
  transform: translateX(-2px);
}

.legal-page__back svg {
  transition: transform var(--transition-fast);
}

.legal-page__back:hover svg {
  transform: translateX(-3px);
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 768px) {
  .legal-page { padding: 48px 0; }

  .legal-page__card {
    padding: var(--space-6);
    margin: 0 var(--space-4);
  }

  .legal-page__title { font-size: var(--font-size-2xl); }
  .legal-page__content h2 { font-size: var(--font-size-base); }
}

@media (max-width: 540px) {
  .legal-page { padding: 32px 0; }

  .legal-page__card {
    padding: var(--space-4);
    margin: 0 var(--space-3);
  }

  .legal-page__title { font-size: var(--font-size-xl); }
  .legal-page__date { font-size: var(--font-size-xs); }
  .legal-page__content { font-size: var(--font-size-sm); }
}
</style>
