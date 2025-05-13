# Course Image Upload Functionality

This Spring Boot application has been updated to support image uploads for course pictures. Below are the details on how to use this functionality.

## Course Image Upload Endpoint

### Create a New Course with Image Upload

**Endpoint:** `POST /api/courses/{teacherId}`  
**Content-Type:** `multipart/form-data`

**Form Data Fields:**
- `title` (required): Course title
- `description` (required): Course description
- `pictureFile` (optional): Image file for the course
- `price` (required): Course price
- `categoryIds` (optional): Array of category IDs
- `tags` (optional): Array of tags

**Example Request using curl:**
```bash
curl -X POST http://localhost:8080/api/courses/1 \
  -H "Content-Type: multipart/form-data" \
  -F "title=My Course" \
  -F "description=Course Description" \
  -F "pictureFile=@/path/to/image.jpg" \
  -F "price=49.99" \
  -F "categoryIds=1" \
  -F "categoryIds=2" \
  -F "tags=Java" \
  -F "tags=Spring"
```

### Create a Course for Request with Image Upload

**Endpoint:** `POST /api/courses/request/{courseRequestId}/teacher/{teacherId}`  
**Content-Type:** `multipart/form-data`

**Form Data Fields:**
- `title` (required): Course title
- `description` (required): Course description
- `pictureFile` (optional): Image file for the course
- `price` (required): Course price
- `categoryIds` (optional): Array of category IDs
- `tags` (optional): Array of tags

## JSON Endpoints (Backward Compatibility)

For backward compatibility, the application still supports JSON-based endpoints:

- `POST /api/courses/{teacherId}/json`
- `POST /api/courses/request/{courseRequestId}/teacher/{teacherId}/json`

## Using Swagger UI

The application includes Swagger UI for API documentation and testing:

1. Access Swagger UI at `http://localhost:8081/swagger-ui.html`
2. For file uploads:
   - Use the `/api/courses/{teacherId}` endpoint
   - Click "Try it out"
   - Fill in the required fields (title, description, price)
   - For pictureFile, click "Browse" to select an image file
   - For categoryIds and tags, you can enter multiple values
   - Execute the request

**Important Note**: When using Swagger UI for file uploads, make sure the Content-Type is set to `multipart/form-data` in the request. Swagger UI should handle this automatically for properly documented endpoints.

## File Storage

Uploaded images are stored in the `uploads` directory. The image URLs are stored in the database as relative paths starting with `/uploads/`.

## Configuration

### Application Properties

The file upload settings are configured in `application.properties`:

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads
```

### Web Configuration

The file upload functionality has been integrated into the existing `WebConfig` class in the `Configuration` package, which also handles security configuration. This ensures there are no conflicts between different configuration classes.

The configuration includes:
- Resource handler mapping for `/uploads/**` path
- MultipartResolver bean for file uploads
- Existing security and CORS configuration 