package hibernate;

import hibernate.model.Employee;
import hibernate.queries.Queries;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;
import java.util.Random;


class Manager {

    public static void main(String[] args) {

        System.out.println("Start");

        EntityManager entityManager = null;

        EntityManagerFactory entityManagerFactory = null;

        try {

            // FACTORY NAME HAS TO MATCHED THE ONE FROM PERSISTED.XML !!!
            entityManagerFactory = Persistence.createEntityManagerFactory("hibernate-dynamic");

            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);

            //New transaction
            session.beginTransaction();

            // Create Employee
            Employee emp = createEmployee();

            // Save in First order Cache (not database yet)
            session.save(emp);

            Employee employee = session.get(Employee.class, emp.getId());
            if (employee == null) {
                System.out.println(emp.getId() + " not found! ");
            } else {
                System.out.println("Found " + employee);
            }
            //session.refresh(employee); //Error object not in DB yet!!

            System.out.println("Employee " + employee.getId() + " " + employee.getFirstName() + employee.getLastName());

            employee.setLastName("NowakPRE" + new Random().nextInt()); // No SQL needed
            changeFirstGuyToNowak(session);// Flush executed

            //Commit transaction to database
            session.getTransaction().commit();

            session.refresh(employee);

            System.out.println("Done");

            session.close();

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
        } finally {
            entityManagerFactory.close();
        }

    }

    private static Employee createEmployee() {
        Employee emp = new Employee();
        emp.setFirstName("Jan");
        emp.setLastName("Polak" + new Random().nextInt());
        emp.setSalary(100);
        emp.setPesel(new Random().nextInt());
        return emp;
    }

    static void changeFirstGuyToNowak(Session session) {

        // Force synchronization
        List<Employee> employees = new Queries(session).getEmployeeByName("Polak");

        employees.get(0).setLastName("NowakPRE" + new Random().nextInt());

    }

}