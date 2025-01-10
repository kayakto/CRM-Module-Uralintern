# Расположение приложения
<p><strong>Backend REST API</strong> находится по адресу:</p>

<a>https://84.252.132.66</a>

<p><strong>Swagger-документация</strong> сервиса расположена по адресу</p>

<a>https://84.252.132.66/swagger-ui/index.html</a>

# Инструкция для запуска(Backend)
1. Скачайте данный репозиторий:
    ```bash
    git clone https://github.com/kayakto/CRM-Module-Uralintern.git
    ```
2. Перейдите в директорию проекта:
    ```bash
    cd CRM-Module-Uralintern
    ```
3. Создайте сертификаты для работы по https
   ```bash
   mkdir certificates
   ```
   ```bash
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout certificates/bytebuilders-selfsigned.key -out certificates/bytebuilders-selfsigned.crt
   ```

4. Сделайте билд и запустите приложение с помощью Docker Compose:
    ```bash
    docker docker-compose up --build -d
    ```

<ul>
    <li>
        <p>Приложение будет доступно по адресу:</p>
        <a href="https://localhost:8080">https://localhost:8080</a>
    </li>
    <li>
        <p>Документация расположена по адресу:</p>
        <a href="https://localhost:8080/swagger-ui/index.html#">https://localhost:8080/swagger-ui/index.html#</a>
    </li>
</ul>

5. Чтобы остановить приложение и удалить контейнеры:

   ```bash
   docker-compose down
   ```
   или

   ```bash
   docker-compose down --remove-orphans
   ```

