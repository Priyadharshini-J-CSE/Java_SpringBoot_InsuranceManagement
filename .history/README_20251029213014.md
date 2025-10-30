# Insurance Claim Processing and Risk Management System

A comprehensive web-based insurance management platform built with Spring Boot, MySQL, and Thymeleaf.

## Features

### User Features
- **Registration with OTP Verification**: Email-based OTP verification during registration
- **Aadhaar Verification**: Validates Aadhaar number and name matching
- **Password Strength Validation**: Real-time password strength checking
- **Policy Management**: Apply for various insurance policies (Health, Life, Vehicle, Home)
- **Claim Submission**: Submit reimbursement claims with document upload
- **Digital Wallet**: Receive approved claim amounts in digital wallet
- **Notifications**: Real-time notifications for policy and claim status updates
- **Dashboard**: Comprehensive overview of policies, claims, and wallet balance

### Admin Features
- **Policy Approval**: Review and approve/reject policy applications
- **Claim Processing**: Review claims, verify documents, and approve payments
- **User Management**: Monitor all user activities and policy statuses
- **Dashboard Analytics**: Overview of pending policies, claims, and system statistics

### Security Features
- **Spring Security**: Role-based authentication and authorization
- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session handling
- **Input Validation**: Server-side validation for all forms

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Java Version**: 17

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Setup Instructions

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE insurance_db;

-- Update application.properties with your MySQL credentials
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Email Configuration
Update `application.properties` with your email credentials for OTP functionality:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 3. Run the Application
```bash
# Clone the repository
cd project3

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 4. Access the Application
- **URL**: http://localhost:8080
- **Admin Login**: 
  - Username: admin
  - Password: admin123

## Project Structure

```
src/
├── main/
│   ├── java/com/insurance/
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data access layer
│   │   ├── service/         # Business logic layer
│   │   ├── controller/      # Web controllers
│   │   ├── config/          # Configuration classes
│   │   └── InsuranceApplication.java
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       ├── static/css/      # CSS files
│       └── application.properties
```

## Key Entities

1. **User**: User management with role-based access
2. **Policy**: Insurance policy management
3. **Claim**: Claim processing and reimbursement
4. **Wallet**: Digital wallet for claim settlements
5. **Notification**: User notification system
6. **OtpVerification**: Email OTP verification

## API Endpoints

### Authentication
- `GET /` - Home page
- `GET /register` - Registration form
- `POST /register` - Process registration
- `GET /verify-otp` - OTP verification form
- `POST /verify-otp` - Verify OTP
- `GET /login` - Login form

### User Dashboard
- `GET /dashboard` - User dashboard
- `GET /apply-policy` - Policy application form
- `POST /apply-policy` - Submit policy application
- `GET /submit-claim` - Claim submission form
- `POST /submit-claim` - Submit claim
- `GET /wallet` - Wallet page
- `GET /notifications` - Notifications page

### Admin Panel
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/policies` - Manage policies
- `POST /admin/approve-policy/{id}` - Approve policy
- `POST /admin/reject-policy/{id}` - Reject policy
- `GET /admin/claims` - Manage claims
- `POST /admin/approve-claim/{id}` - Approve claim
- `POST /admin/reject-claim/{id}` - Reject claim

## Business Logic

### Registration Flow
1. User fills registration form with Aadhaar details
2. System validates Aadhaar number and name
3. Password strength is checked in real-time
4. OTP is sent to user's email
5. User verifies OTP to complete registration
6. Digital wallet is automatically created

### Policy Application Flow
1. User applies for policy with required details
2. Application is submitted with PENDING status
3. Admin reviews and approves/rejects policy
4. User receives notification of decision
5. Approved policies become ACTIVE

### Claim Processing Flow
1. User submits claim with supporting documents
2. Claim enters PENDING status for admin review
3. Admin reviews documents and policy validity
4. Admin approves with amount or rejects with reason
5. Approved amount is credited to user's wallet
6. User receives notification of claim decision

## Security Considerations

- All passwords are encrypted using BCrypt
- Role-based access control (USER/ADMIN)
- Input validation and sanitization
- Secure file upload handling
- Session timeout management
- CSRF protection disabled for API endpoints

## Future Enhancements

- Integration with real Aadhaar verification API
- Payment gateway integration for premium payments
- Advanced fraud detection algorithms
- Mobile application support
- Real-time chat support
- Document verification using AI/ML
- Advanced reporting and analytics

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running
   - Check database credentials in application.properties

2. **Email Not Sending**
   - Verify email configuration
   - Enable "Less secure app access" for Gmail
   - Use App Password for Gmail

3. **File Upload Issues**
   - Check file size limits in application.properties
   - Ensure upload directory permissions

## License

This project is developed for educational purposes as part of the Java LEAP Project curriculum.

## Contact

For any queries or support, please contact the development team.
=======
[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/MiEONeiU)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=21352702&assignment_repo_type=AssignmentRepo)
>>>>>>> 3a1c8856981d604f1ec6436297a05ad141da446a
