package com.chatbot.store;

import com.chatbot.model.Faq;
import com.chatbot.util.Json;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Stores FAQs in faqs.json and unanswered questions in unanswered.json. */
public class FaqStore {

    private static final Object LOCK = new Object();

    // ---------- FAQs ----------
    public List<Faq> getAll() throws IOException {
        synchronized (LOCK) {
            Path p = DataDir.file("faqs.json");
            if (!Files.exists(p)) {
                saveFaqs(seed());
            }
            try {
                List<Faq> list = new ArrayList<>();
                for (Map<String, String> m : Json.parse(DataDir.read(p))) {
                    list.add(new Faq(Integer.parseInt(m.get("id")), m.get("question"),
                                     m.get("keywords"), m.get("answer")));
                }
                return list;
            } catch (RuntimeException e) {
                throw new IOException("faqs.json is not valid JSON", e);
            }
        }
    }

    public Faq get(int id) throws IOException {
        for (Faq f : getAll()) if (f.getId() == id) return f;
        return null;
    }

    public void insert(Faq f) throws IOException {
        synchronized (LOCK) {
            List<Faq> list = getAll();
            int max = 0;
            for (Faq x : list) max = Math.max(max, x.getId());
            f.setId(max + 1);
            list.add(f);
            saveFaqs(list);
        }
    }

    public void update(Faq f) throws IOException {
        synchronized (LOCK) {
            List<Faq> list = getAll();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == f.getId()) list.set(i, f);
            }
            saveFaqs(list);
        }
    }

    public void delete(int id) throws IOException {
        synchronized (LOCK) {
            List<Faq> list = getAll();
            list.removeIf(x -> x.getId() == id);
            saveFaqs(list);
        }
    }

    private void saveFaqs(List<Faq> list) throws IOException {
        StringBuilder b = new StringBuilder("[\n");
        for (int i = 0; i < list.size(); i++) {
            Faq f = list.get(i);
            b.append("  {\"id\": ").append(f.getId())
             .append(", \"question\": ").append(Json.quote(f.getQuestion()))
             .append(", \"keywords\": ").append(Json.quote(f.getKeywords()))
             .append(", \"answer\": ").append(Json.quote(f.getAnswer())).append("}")
             .append(i < list.size() - 1 ? ",\n" : "\n");
        }
        DataDir.write(DataDir.file("faqs.json"), b.append("]").toString());
    }

    // ---------- unanswered questions ----------
    /** Remembers questions the bot could not answer (latest first, max 50). */
    public void logUnanswered(String question) throws IOException {
        synchronized (LOCK) {
            List<String> list = unanswered(50);
            list.remove(question);
            list.add(0, question);
            while (list.size() > 50) list.remove(list.size() - 1);
            StringBuilder b = new StringBuilder("[\n");
            for (int i = 0; i < list.size(); i++) {
                b.append("  {\"question\": ").append(Json.quote(list.get(i))).append("}")
                 .append(i < list.size() - 1 ? ",\n" : "\n");
            }
            DataDir.write(DataDir.file("unanswered.json"), b.append("]").toString());
        }
    }

    public List<String> unanswered(int limit) throws IOException {
        synchronized (LOCK) {
            Path p = DataDir.file("unanswered.json");
            List<String> list = new ArrayList<>();
            if (!Files.exists(p)) return list;
            try {
                for (Map<String, String> m : Json.parse(DataDir.read(p))) {
                    if (list.size() < limit) list.add(m.get("question"));
                }
            } catch (RuntimeException e) {
                throw new IOException("unanswered.json is not valid JSON", e);
            }
            return list;
        }
    }

    // ---------- sample data ----------
    private List<Faq> seed() {
        String[][] d = {
            {"How can I apply for admission?", "admission,apply,application,eligibility,enroll,join",
             "You can apply online through the college website admission portal. Fill the form, upload your marksheets and pay the application fee. Our admission office will contact you for counselling."},
            {"What courses are offered?", "course,courses,program,programs,branch,degree,mca,btech,bca",
             "We offer undergraduate and postgraduate programs including BCA, B.Tech, MCA and MBA. Visit the Courses page on the website for the full list and syllabus."},
            {"What are the fees?", "fee,fees,tuition,cost,payment,charges",
             "Fees depend on the course you choose. Please check the Fee Structure section of the website or contact the accounts office for the exact amount and installment options."},
            {"Is hostel facility available?", "hostel,accommodation,stay,room,mess,pg",
             "Yes, separate hostels for boys and girls are available on campus with Wi-Fi, mess and 24x7 security. Apply for a room from the hostel office after confirming admission."},
            {"What about placements?", "placement,placements,job,jobs,package,recruiter,company,career",
             "The Training and Placement Cell conducts aptitude training, mock interviews and campus drives with reputed companies every year."},
            {"Are scholarships available?", "scholarship,scholarships,merit,financial aid,discount,concession",
             "Yes, merit-based and need-based scholarships are available. Contact the scholarship cell with your marksheets and income proof for details."},
            {"What are the college timings?", "timing,timings,time,hours,schedule,open,working hours",
             "Classes run from Monday to Friday, 9:00 AM to 4:00 PM. The administrative office is open from 9:00 AM to 5:00 PM."},
            {"Is there a library?", "library,books,study,reading room,journals",
             "Yes, the central library has thousands of books, journals and a digital reading section. Students can borrow books using their ID card."},
            {"How can I contact the college?", "contact,phone,email,address,location,reach,call",
             "You can call the help desk or email the admission office. The address, phone number and email are available in the Contact Us section of the college website."},
            {"When are the exams held?", "exam,exams,examination,test,result,results,datesheet",
             "Mid-semester and end-semester exams are held as per the academic calendar. The date sheet and results are published on the student portal."}
        };
        List<Faq> list = new ArrayList<>();
        for (int i = 0; i < d.length; i++) list.add(new Faq(i + 1, d[i][0], d[i][1], d[i][2]));
        return list;
    }
}
