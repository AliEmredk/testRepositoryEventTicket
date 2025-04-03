package bll;

import be.Customer;
import dal.CustomerDAO;

import java.util.List;

    public class CustomerManager {
        private CustomerDAO customerDAO;

        public CustomerManager() {
            customerDAO = new CustomerDAO();
        }

        public List<Customer> getAllCustomers() {
            return customerDAO.getAllCustomers();
        }

        public Customer getCustomerById(int id) {
            return customerDAO.getCustomerById(id);
        }

        public void addCustomer(Customer customer) {
            customerDAO.addCustomer(customer);
        }

        public void updateCustomer(Customer customer) {
            customerDAO.updateCustomer(customer);
        }

        public void deleteCustomer(int id) {
            customerDAO.deleteCustomer(id);
        }
    }

