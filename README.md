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

## DB (check in docker): http://localhost:54823/
- 'POSTGRES_DB=barber_db'
- 'POSTGRES_PASSWORD=secret'
- 'POSTGRES_USER=myuser'

## Backend:
http://localhost:8443/api/services 

Jeśli baza jest dostępna powyższy link powinien zwrócić: 
```
[{"serviceId":1,"name":"Haircut","description":"Basic haircut","durationMinutes":30,"price":50.0,"isActive":true},{"serviceId":2,"name":"Beard Trim","description":"Beard shaping","durationMinutes":20,"price":30.0,"isActive":true}]
```

### Endpointy (TODO):

`/api/auth/me` -> Dostępne w aplikacji jeśli użytkownik jest zalogowany (Home screen -> settings -> przycisk "check my profile info")
```java
return new AuthResponse(
    null,
    user.getUserId(),
    user.getName(),
    user.getEmail(),
    user.getPhone(),
    user.getRole()
);
```
