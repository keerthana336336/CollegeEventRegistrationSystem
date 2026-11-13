package com.collegeevent.dao;

import com.collegeevent.model.Event;
import com.collegeevent.util.AppConfig;
import com.collegeevent.util.PasswordUtil;

import java.time.LocalDate;
import java.util.logging.Logger;

/** First-run data: the admin account and a few sample events. */
public final class DataSeeder {
    private static final Logger LOG = Logger.getLogger(DataSeeder.class.getName());

    private DataSeeder() { }

    public static void seed() {
        DataStore store = DataStore.get();
        UserDao users = new UserDao();
        if (users.ensureAdminAccount(AppConfig.adminEmail(), "Administrator", AppConfig.adminPassword())) {
            LOG.info("Admin account created or repaired: " + AppConfig.adminEmail());
        }
        if (store.isFirstRun()) {
            EventDao events = new EventDao();
            events.create(sample("CodeSprint Hackathon 2026", "Technical", 9,
                    "09:00", "18:00", "Innovation Lab, Block C", "Computer Science Club", 120,
                    "A nine-hour build sprint. Form teams of up to four, pick a problem statement and demo a working prototype to a panel of faculty and industry mentors. Snacks and lunch included."));
            events.create(sample("Rhythm & Roots Cultural Night", "Cultural", 13,
                    "17:30", "21:00", "Open Air Auditorium", "Cultural Committee", 300,
                    "An evening of dance, music and theatre from every department. Bring a friend, arrive early for seats near the stage."));
            events.create(sample("Inter-Department Cricket Cup", "Sports", 16,
                    "08:00", "16:00", "Main Sports Ground", "Sports Council", 80,
                    "Knock-out T10 matches between departments. Register as an individual and the sports council will place you in your department squad."));
            events.create(sample("Cloud and DevOps Hands-on Workshop", "Workshop", 11,
                    "10:00", "13:00", "Computer Lab 3", "IT Department", 40,
                    "Deploy a small web service end to end: containers, a CI pipeline and a free-tier cloud account. Bring your own laptop."));
            events.create(sample("Careers in AI: Alumni Talk", "Career", 21,
                    "15:00", "16:30", "Seminar Hall A", "Training and Placement Cell", 150,
                    "Three alumni working in machine learning explain how they got there, what interviews look like and which skills to build in your final two years."));
            events.create(sample("Resume and Interview Bootcamp", "Career", 6,
                    "14:00", "17:00", "Placement Cell Conference Room", "Training and Placement Cell", 30,
                    "Small-group resume reviews followed by mock interviews with feedback. Bring a printed copy of your resume."));
            LOG.info("Added sample events (first run).");
        }
    }

    private static Event sample(String title, String category, int daysFromToday, String start, String end,
                                String venue, String organizer, int capacity, String description) {
        LocalDate date = LocalDate.now().plusDays(daysFromToday);
        Event e = new Event();
        e.setTitle(title);
        e.setCategory(category);
        e.setDate(date.toString());
        e.setStartTime(start);
        e.setEndTime(end);
        e.setVenue(venue);
        e.setOrganizer(organizer);
        e.setCapacity(capacity);
        e.setRegistrationDeadline(date.minusDays(1).toString() + "T18:00");
        e.setDescription(description);
        return e;
    }
}
