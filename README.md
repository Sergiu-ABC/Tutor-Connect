# Tutor Connect

A booking platform where UTCN students find tutors, filter them by teaching language, pay from a
virtual wallet and leave ratings. It has a web interface and a console client, both on top of the
same PostgreSQL database.

The web layer is written without a framework: the JDK's built-in `HttpServer`, hand-written request
routing and form parsing, and HTML rendered on the server.

## Features

- Register and log in, with a starting wallet balance of 100 RON that can be topped up
- Browse tutors with their languages and levels, and filter by language
- Book a session for a date and number of hours; the tutor and the student must both be free that day
- Cancel a booking and get the money back automatically
- Rate a tutor (1 to 5 stars) with a comment; the average rating shows on the tutor's card

## How bookings stay consistent

Paying and saving the appointment happen in one database transaction
(`TutorRepository.bookAppointment`):

1. `UPDATE users SET budget = budget - cost WHERE username = ? AND budget >= cost`
   only succeeds if the money is still there at that exact moment, so two quick bookings can't
   spend the same balance twice.
2. The appointment is inserted.
3. Commit. If anything fails, the transaction rolls back and the student is not charged.

Cancelling works the same way in reverse: the appointment is deleted only if it belongs to the
logged-in student (`DELETE ... RETURNING total_cost`), and the refund is part of the same transaction.

## Project structure

```
src/main/java/
├── WebServer.java          starts the HTTP server on port 8080
├── TutorController.java    routes requests (login, register, book, cancel, review, top-up)
├── HtmlView.java           renders the login, main and profile pages
├── TutorRepository.java    all SQL: users, tutors, bookings, reviews
├── DatabaseConnection.java one shared JDBC connection, configured from environment variables
├── Demo.java               console client
└── Person, Users, Tutors, Appointment, Review   domain classes
```

## Running it

Requirements: Java 25, Maven, PostgreSQL.

```bash
createdb tutor_db
psql -d tutor_db -f schema.sql      # tables and a few sample tutors (for a fresh database)

# optional, these are the defaults
export DB_URL=jdbc:postgresql://localhost:5432/tutor_db
export DB_USER=user
export DB_PASSWORD=password

mvn compile
mvn exec:java -Dexec.mainClass=WebServer   # then open http://localhost:8080
```

Run `Demo` instead of `WebServer` for the console client.

## Next steps

- Per-browser sessions (cookies) instead of a single logged-in user on the server
- Hash passwords (bcrypt) instead of storing them as plain text
- Escape user input in the rendered HTML
