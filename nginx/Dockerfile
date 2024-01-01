# Используем базовый образ с NGINX
FROM nginx:latest

# Копирование конфигурационных файлов NGINX внутрь контейнера
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Опционально: копирование статических файлов, если они нужны для NGINX
# COPY static-content /usr/share/nginx/html

# Указание порта, который будет использоваться NGINX
EXPOSE 80