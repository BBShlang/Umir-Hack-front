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
          <h1 class="legal-page__title">Политика конфиденциальности</h1>
          <p class="legal-page__date">Последнее обновление: 4 апреля 2026 г.</p>

          <div class="legal-page__content">

            <section>
              <h2>1. Общие положения</h2>
              <p>Настоящая Политика конфиденциальности (далее — «Политика») определяет порядок обработки и защиты персональных данных пользователей платформы «ДиплоРеестр» (далее — «Платформа», «Сервис», «мы»), являющейся системой верификации дипломов и управления реестром документов об образовании.</p>
              <p>Используя Сервис, вы соглашаетесь с условиями настоящей Политики. Если вы не согласны с какими-либо положениями, пожалуйста, воздержитесь от использования Сервиса.</p>
            </section>

            <section>
              <h2>2. Сбор и обработка персональных данных</h2>
              <p>Мы собираем и обрабатываем следующие категории персональных данных:</p>
              <ul>
                <li><strong>Идентификационные данные:</strong> ФИО, адрес электронной почты, пароль (в хэшированном виде).</li>
                <li><strong>Данные об образовании:</strong> номер диплома, специальность, наименование ВУЗа, дата выдачи, статус диплома.</li>
                <li><strong>Технические данные:</strong> IP-адрес, тип браузера, данные cookies, журнал обращений к API.</li>
                <li><strong>Данные верификации:</strong> история проверок дипломов работодателями, временные метки запросов.</li>
              </ul>
              <p>Обработка персональных данных осуществляется на основании вашего согласия (ст. 6 ФЗ-152 «О персональных данных»), а также в целях исполнения договора и законных интересов оператора.</p>
            </section>

            <section>
              <h2>3. Цели обработки данных</h2>
              <p>Персональные данные обрабатываются исключительно в следующих целях:</p>
              <ul>
                <li>Регистрация и аутентификация пользователей (ВУЗы, студенты, работодатели).</li>
                <li>Ведение реестра дипломов и обеспечение их верификации.</li>
                <li>Генерация временных ссылок и QR-кодов для предоставления доступа к данным о дипломе.</li>
                <li>Обеспечение безопасности и предотвращение мошенничества.</li>
                <li>Предоставление API-интерфейса для автоматизированной проверки дипломов.</li>
                <li>Улучшение качества Сервиса и технической поддержки пользователей.</li>
              </ul>
            </section>

            <section>
              <h2>4. Хранение и защита данных</h2>
              <p>Мы применяем организационные и технические меры для защиты ваших данных:</p>
              <ul>
                <li><strong>Шифрование:</strong> пароли хранятся в хэшированном виде; данные передаются по защищённым каналам (TLS/SSL).</li>
                <li><strong>Цифровые подписи:</strong> каждая запись реестра подписывается электронной подписью ВУЗа для обеспечения целостности.</li>
                <li><strong>Ограничение доступа:</strong> доступ к персональным данным предоставляется только уполномоченным сотрудникам.</li>
                <li><strong>Временные ссылки:</strong> ссылки для работодателей имеют ограниченный срок действия и автоматически деактивируются.</li>
              </ul>
              <p>Данные хранятся на серверах, расположенных на территории Российской Федерации, в соответствии с требованиями ФЗ-152.</p>
            </section>

            <section>
              <h2>5. Передача данных третьим лицам</h2>
              <p>Мы не передаём, не продаём и не раскрываем ваши персональные данные третьим лицам, за исключением следующих случаев:</p>
              <ul>
                <li><strong>По вашему согласию:</strong> когда студент генерирует ссылку или QR-код для работодателя.</li>
                <li><strong>По требованию закона:</strong> по запросу правоохранительных или судебных органов в порядке, установленном законодательством РФ.</li>
                <li><strong>Технические подрядчики:</strong> хостинг-провайдеры и сервисы аналитики, действующие в соответствии с нашими инструкциями и обязательствами о конфиденциальности.</li>
              </ul>
            </section>

            <section>
              <h2>6. Права пользователей</h2>
              <p>В соответствии с ФЗ-152 «О персональных данных» вы имеете право:</p>
              <ul>
                <li>Получить информацию о том, какие ваши данные обрабатываются.</li>
                <li>Требовать уточнения, блокирования или уничтожения ваших персональных данных.</li>
                <li>Отозвать согласие на обработку данных, направив запрос на <a href="mailto:support@diploms.ru">support@diploms.ru</a>.</li>
                <li>Обжаловать действия или бездействие оператора в уполномоченном органе по защите прав субъектов персональных данных или в судебном порядке.</li>
              </ul>
            </section>

            <section>
              <h2>7. Cookies и аналитика</h2>
              <p>Сервис может использовать файлы cookies для обеспечения корректной работы, аутентификации и анализа трафика. Вы можете управлять настройками cookies в вашем браузере. Отключение cookies может повлиять на функциональность отдельных разделов Сервиса.</p>
            </section>

            <section>
              <h2>8. Изменение Политики</h2>
              <p>Мы оставляем за собой право вносить изменения в настоящую Политику. Актуальная версия всегда доступна на данной странице. Дата последнего обновления указана в начале документа. Продолжая использовать Сервис после внесения изменений, вы принимаете обновлённую Политику.</p>
            </section>

            <section>
              <h2>9. Контактная информация</h2>
              <p>По вопросам, связанным с обработкой персональных данных, обращайтесь:</p>
              <ul>
                <li>Email: <a >support@diploms.ru</a></li>
                <li>Телефон: <a>8 (800) 123-45-67</a></li>
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
