package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.UserDao;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.EventView;
import com.collegeevent.model.RegistrationView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "StudentDashboardServlet", urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = currentUserId(req);
        ViewBuilder vb = new ViewBuilder();
        List<RegistrationView> all = vb.userRegistrations(uid);

        List<RegistrationView> upcoming = new ArrayList<RegistrationView>();
        int active = 0, attended = 0;
        for (RegistrationView r : all) {
            if (!r.getRegistration().isActive()) continue;
            active++;
            if (r.getRegistration().isAttended()) attended++;
            if (!r.isEventEnded()) upcoming.add(r);
        }
        Collections.sort(upcoming, new Comparator<RegistrationView>() {
            @Override public int compare(RegistrationView a, RegistrationView b) {
                String x = a.getEvent().getDate() + " " + a.getEvent().getStartTime();
                String y = b.getEvent().getDate() + " " + b.getEvent().getStartTime();
                return x.compareTo(y);
            }
        });

        List<EventView> suggestions = new ArrayList<EventView>();
        for (EventView v : vb.eventViews(new EventDao().search(null, null, true), uid)) {
            if (v.isOpen() && !v.isUserRegistered()) suggestions.add(v);
            if (suggestions.size() == 3) break;
        }

        req.setAttribute("profile", new UserDao().findById(uid));
        req.setAttribute("activeCount", active);
        req.setAttribute("attendedCount", attended);
        req.setAttribute("upcomingCount", upcoming.size());
        req.setAttribute("upcomingRegs", upcoming.size() > 4 ? upcoming.subList(0, 4) : upcoming);
        req.setAttribute("suggestions", suggestions);
        forward(req, resp, "student/dashboard.jsp");
    }
}
