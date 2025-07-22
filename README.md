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

## Word Management
**1. Insert Word**
```
curl --location 'http://localhost:8080/word/insertWord' \
--header 'Content-Type: application/json' \
--data '{
    "context": "example context", // context sentence
    "englishWord": "hello",
    "vietnameseWord": "xin chào", // translate from frontend
    "isExtension": false, // true: insert from the extension
    "note": "sample note", // user can note something on this word
    "userId": "123"
  }'
```

**2. Edit Word**
```
curl --location 'http://localhost:8080/word/editWord' \
--header 'Content-Type: application/json' \
--data '{
    "wordId": "77cb390b-8b2b-4154-8134-a524b1641bc8",
    "context": "example context1234",
    "englishWord": "hello1234",
    "vietnameseWord": "xin chào1234",
    "isExtension": true,
    "note": "sample note 1234"
}'
```

