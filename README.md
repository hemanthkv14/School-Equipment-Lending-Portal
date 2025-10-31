 cd selp-api-gateway
 Run npm run dev
 API Gateway  listening on http://localhost:8081



 cd selp-auth-service 
 Run nom run dev
 Auth service listening on http://localhost:4000


 cd  selp-client-service
 Run npm start
 Client service listening on http://localhost:3000


cd selp-equipment-service
mvn spring-boot:run
Equipment service listening on http://localhost:8080
In the current setup , connected to localhost postgres database

Made slight change to 1 field is users table . This change is to make user_id auto generated

ALTER TABLE users ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY;
ALTER TABLE notifications ALTER COLUMN message TYPE TEXT;
 
