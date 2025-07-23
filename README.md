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

**3. Reset Password**
```
curl --location 'localhost:8080/user/reset-password' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "tandungnguyen918@gmail.com",
    "password": "1234567",
    "code": "156569"
}'
```

## System Organization
### 1. Notification
![Notification Organization](./assets/NotificationOrganization.png)
**How to use**

*1. Inject notificationFactory and notificationProcessorFactory*
```
@Autowired 
NotificationFactory notificationFactory;

@Autowired 
NotificationProcessorFactory notificationProcessorFactory;
```
*2. Call function to handle logic*
```
- Notification notification = this.notificationFactory.createNotification(NotificationEnum.Mail);
- NotificationProcessor notificationProcessor = this.notificationProcessorFactory.createProcessor(NotificationEnum.Mail);
notificationProcessor.process(notification);
```