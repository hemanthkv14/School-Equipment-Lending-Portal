run runAllServices.sh this would do the following . Basically it would bring up all the services . Before running this make sure that the .env has the corret details of the postgressql details. Currenlty its connectedto local postgressql
 
 cd selp-api-gateway
 Run npm run dev
 API Gateway  listening on http://localhost:8081



 cd selp-auth-service 
 Run npm run dev
 Auth service listening on http://localhost:4000


 cd  selp-client-service
 Run npm start
 Client service listening on http://localhost:3000


cd selp-equipment-service
mvn spring-boot:run
Equipment service listening on http://localhost:8080
In the current setup , connected to localhost postgres database

cd selp-notification-service 
Run npm run dev
This service would run as a cron , that check the duedate and sends an email 2 days before and send the details to notification table . If a message is send it wont send the message again

Created 
Made slight change to 1 field is users table . This change is to make user_id auto generated

ALTER TABLE users ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE notifications ALTER COLUMN message TYPE TEXT;
 
