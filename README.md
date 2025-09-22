Election Management App – Description

The Election Management App is an Android-based application designed to simplify the process of creating, managing, and monitoring elections for administrators. It provides a user-friendly interface to add elections, track their status (Upcoming, Ongoing, or Ended), and manage candidates for each election. This app is ideal for small organizations, educational institutions, or communities that need a streamlined digital solution for election management.

Key Features

Add Elections Easily

Admin can add new elections with title, state, start date & time, and end date & time.

Date and time are selected via interactive DatePicker and TimePicker dialogs.

Start and end times are saved in ISO format to avoid inconsistencies.

Election List & Status Tracking

All elections are displayed in a clean RecyclerView list.

Each election shows:

Title

State

Start and End Time

Status (Upcoming, Ongoing, Ended)

Status updates automatically based on the current date and time.

Manage Elections

Tap on an election to view the list of candidates.

Long press on an election to Edit or Delete it.

Delete operations immediately remove the election from the database and UI.

Database Integration

Uses SQLite database to store elections securely.

Stores election title, start & end times, and state.

Ensures that no election data is lost between app restarts.

Date & Time Utilities

Includes DateTimeUtils class for date formatting, parsing, and status calculation.

Supports automatic conversion and display of election status based on ISO formatted dates.

Modern UI Support

Implements Edge-to-Edge design for a modern Android look.

Proper handling of system bars and safe area insets.

Clean and responsive design using RecyclerView.

How It Works

Add Election: Admin clicks Add Election, enters details, picks start & end time, and saves.

View Elections: The main screen shows all elections with their current status.

Track Status: Elections automatically update their status:

Upcoming – current time is before the start time.

Ongoing – current time is between start and end times.

Ended – current time is after the end time.

Manage Elections: Admin can delete elections directly or eventually edit them in future updates.

Technology Stack

Language: Java

Database: SQLite

Android Components:

RecyclerView for listing elections

Edge-to-Edge UI support

DatePicker & TimePicker

AlertDialog for options

Utilities: Custom DateTimeUtils for handling ISO date-time formatting and election status calculation.
