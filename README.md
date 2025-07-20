# word-wise

## API Documents - Version 1.0

## Account Management
**1. Sign Up**
```
curl --location 'localhost:8080/user/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "tandungnguyen123@gmail.com",
    "password": "12345"
}'
```

**2. Login**
```
curl --location 'localhost:8080/user/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "tandungnguyen123@gmail.com",
    "password": "12345"
}'
```

