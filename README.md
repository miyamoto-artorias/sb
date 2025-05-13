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
- `categoryIds` (optional): Array of category IDs (repeat parameter for multiple values)
- `tags` (optional): Array of tags (repeat parameter for multiple values)

**Example Request using curl:**
```bash
curl -X POST http://localhost:8081/api/courses/1 \
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
- `categoryIds` (optional): Array of category IDs (repeat parameter for multiple values)
- `tags` (optional): Array of tags (repeat parameter for multiple values)

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
   - For categoryIds and tags arrays:
     - Enter a value and press Enter to add each array item
     - Swagger will automatically create repeated parameters for each array item
     - This ensures they are sent correctly as form parameters
   - Execute the request

**Important Note for Arrays**: When using arrays with multipart/form-data, each array item needs to be a separate form field with the same name. In Swagger UI, you should add multiple values by adding them one by one to the form field.

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