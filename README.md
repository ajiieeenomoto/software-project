# RTU Event Registration System
### Java Swing + MySQL | Capstone Project

---

## Technologies Used
| Layer | Technology |
|-------|-----------|
| GUI | Java Swing (JFrame, JPanel, CardLayout, JTable) |
| Database | MySQL 8.x |
| Connectivity | JDBC (mysql-connector-j) |
| Password Security | jBCrypt |
| QR Code (optional) | ZXing (core + javase) |
| Reports (optional) | JasperReports |
| IDE | NetBeans or IntelliJ IDEA |

---

## Required Libraries (place in /lib folder)
1. **mysql-connector-j-8.x.x.jar** — JDBC driver  
   Download: https://dev.mysql.com/downloads/connector/j/
2. **jbcrypt-0.4.jar** — BCrypt password hashing  
   Download: https://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.4
3. **core-3.5.2.jar** + **javase-3.5.2.jar** — ZXing QR Code (optional)  
   Download: https://mvnrepository.com/artifact/com.google.zxing

---

## Database Setup

1. Install MySQL 8.x
2. Open MySQL Workbench or terminal
3. Run the schema:
   ```sql
   SOURCE /path/to/database_schema.sql;
   ```
4. Insert your admin account:
   ```sql
   -- First, hash a password using BCrypt (12 rounds), then:
   USE rtu_event_system;
   UPDATE admins SET password_hash = '<your_bcrypt_hash>' WHERE username = 'admin';
   ```
   Or use the provided PasswordUtil to generate a hash programmatically.

---

## Configuration

Edit `src/database/DatabaseConnection.java`:
```java
private static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DATABASE = "rtu_event_system";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

---

## Project Structure

```
RTUEventSystem/
├── lib/                        ← External .jar files
├── database_schema.sql         ← MySQL schema
├── README.md
└── src/
    ├── database/
    │   ├── DatabaseConnection.java   ← JDBC singleton
    │   ├── StudentDAO.java           ← Student CRUD
    │   ├── AdminDAO.java             ← Admin auth
    │   ├── EventDAO.java             ← Event CRUD
    │   ├── RegistrationDAO.java      ← Registration + Attendance
    │   └── NotificationDAO.java      ← Notifications
    ├── models/
    │   ├── Student.java
    │   ├── Admin.java
    │   ├── Event.java
    │   └── Registration.java
    ├── controllers/
    │   └── AuthController.java       ← Login logic
    ├── ui/
    │   ├── shared/
    │   │   └── LandingWindow.java    ← First screen
    │   ├── student/
    │   │   ├── StudentLoginForm.java
    │   │   ├── StudentSignupForm.java
    │   │   └── StudentDashboard.java ← Home, Browse, My Regs, Profile
    │   └── admin/
    │       ├── AdminLoginForm.java
    │       └── AdminDashboard.java   ← Home, Events, Students, Attendance
    ├── utils/
    │   ├── UIConstants.java          ← Colors, fonts, factory methods
    │   ├── PasswordUtil.java         ← BCrypt wrapper
    │   ├── InputValidator.java       ← Validation helpers
    │   └── SessionManager.java       ← Current user session
    └── main/
        └── Main.java                 ← Entry point
```

---

## How to Compile & Run (Command Line)

```bash
# From the project root directory
javac -cp ".:lib/*" -d out -sourcepath src $(find src -name "*.java")
java  -cp ".:lib/*:out" main.Main
```

**Windows:** replace `:` with `;` in classpath.

---

## NetBeans Setup

1. File → New Project → Java → Java Application
2. Copy source files into `src/`
3. Right-click project → Properties → Libraries → Add JAR/Folder
   - Add all JARs from `/lib`
4. Set Main Class to `main.Main`
5. Run (F6)

---

## Functional Flow

```
Student:
  Sign Up → Login → Dashboard → Browse Events → Register
  → QR Pass generated → Admin scans QR → Attendance recorded

Admin:
  Login → Dashboard → Create/Edit Events → View Participants
  → Verify QR Attendance → Manual Mark Attendance → Reports
```

---

## Security Features
- ✅ BCrypt password hashing (12 work factor)
- ✅ PreparedStatement for all SQL (no injection)
- ✅ Role-based access (Student / Admin)
- ✅ Input validation (email, student number, password strength)
- ✅ Session management via SessionManager singleton
- ✅ UUID-based unique QR tokens

---

## Default Admin Credentials
After running the schema, insert an admin with a hashed password. Sample test account:
- Username: `admin`  
- Password: `Admin@RTU2024` (hash this using BCrypt before inserting)

---

*Rizal Technological University — Software Design Capstone Project*
