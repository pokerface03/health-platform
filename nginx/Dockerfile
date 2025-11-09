
FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY index.html /etc/nginx/html/index.html
COPY sea-edge-79ab30e2.png /etc/nginx/html/sea-edge-79ab30e2.png

COPY ./fullchain.pem /etc/ssl/certs/fullchain.pem
COPY ./privkey.pem /etc/ssl/private/privkey.pem

# Ensure correct permissions (NGINX runs as www-data)
RUN chmod 600 /etc/ssl/private/privkey.pem && \
    chmod 644 /etc/ssl/certs/fullchain.pem

EXPOSE 80 443

ENTRYPOINT ["nginx", "-g", "daemon off;"]
