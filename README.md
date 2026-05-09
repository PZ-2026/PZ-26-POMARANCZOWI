System do rezerwacji wizyt w salonie fryzjerskim.

# Uruchamianie projektu

Backend:
```
cd backend
docker-compose up --build
```

Note: Jeśli występują błędy spróbuj `docker-compose down -v` i ponownie `docker-compose up --build`

Frontend:
Android Studio → Run app

# Użytkownicy

## Admin
Login: `admin@test.com`

Hasło: `password1`

## Barber
Login: `barber1@test.com`

Hasło: `password2`

# Użytkownik
Login: `client1@test.com`

Hasło: `password3`

# Linki

Backend:
http://localhost:8443/api/services

DB (check in docker): http://localhost:54823/
- 'POSTGRES_DB=barber_db'
- 'POSTGRES_PASSWORD=secret'
- 'POSTGRES_USER=myuser'
