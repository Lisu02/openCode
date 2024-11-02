# Używamy lekkiego obrazu Alpine
FROM alpine:latest

# Instalujemy gcc oraz time
RUN apk update && \
    apk add --no-cache gcc libc-dev time

# Tworzymy katalog tmp i ustawiamy odpowiednie uprawnienia
RUN mkdir /tmp && chmod 777 /tmp

# Ustawiamy użytkownika nieuprzywilejowanego
RUN adduser -D -h /home/user user
USER user

# Ustawiamy tmp jako jedyny katalog zapisywalny
VOLUME /tmp
