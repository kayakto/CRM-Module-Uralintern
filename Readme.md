# Инструкция для запуска(Backend)
1. Скачайте данный репозиторий:
    ```bash
    git clone https://github.com/kayakto/CRM-Module-Uralintern.git
    ```
2. Перейдите в директорию проекта:
    ```bash
    cd CRM-Module-Uralintern
    ```
3. Сделайте билд и запустите приложение с помощью Docker Compose:
    ```bash
    docker compose up -d
    ```
<p>Приложение будет доступно по адресу http://localhost:8080</p>
<p>Документация расположена по адресу http://localhost:8080/swagger-ui/index.html# </p>
Чтобы остановить приложение и удалить контейнеры:
```bash
docker-compose down
```
или
```bash
docker-compose down --remove-orphans
```

