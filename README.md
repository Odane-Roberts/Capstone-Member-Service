
# Member-Service

The Member Service is a microservice designed to manage member operations, . It provides a RESTful API for interacting with the member database.


## Run Application

###  Package Application with maven 

```bash
  mvn clean package
```

### Launch docker to build and run image 

```bash
  docker-compose up
```
## API Reference

Service will be runnnig on PORT 8092

GATEWAY PORT IS 8222
#### Get all member

```http
  GET /api/v1/member
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `bearer_token` | `string` | **Required**. Your API key from keycloak|

#### Get member by id 

```http
  GET /api/v1/member/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |
| `bearer_token`| `id`| **Required**. Your API key from keycloak|



#### ** Check Controller class for more endpoints


