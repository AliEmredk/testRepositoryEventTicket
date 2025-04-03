package dal;

import be.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private final DBAccess dbAccess = new DBAccess();

    // Method to add a customer to the database
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (FirstName, LastName, Email) VALUES (?, ?, ?)";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());

            stmt.executeUpdate();

            // Retrieve generated CustomerId
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setCustomerId(rs.getInt(1)); // Set the generated CustomerId
                    System.out.println("Customer added successfully with ID: " + customer.getCustomerId());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get a customer by their ID
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM Customer WHERE CustomerId = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("CustomerId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get all customers from the database
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM Customer";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("CustomerId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    // Method to delete a customer from the database
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM Customer WHERE CustomerId = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Customer deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update an existing customer's information
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE Customer SET FirstName = ?, LastName = ?, Email = ? WHERE CustomerId = ?";

        try (Connection conn = dbAccess.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setInt(4, customer.getCustomerId()); // Make sure you set the correct CustomerId

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("No customer found with ID: " + customer.getCustomerId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
