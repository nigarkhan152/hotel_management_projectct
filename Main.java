import java.sql.*;
import java.util.Scanner;
public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Nigar@002";
        public static void main(String[] args) throws ClassNotFoundException, SQLException {
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
            try{
                Connection connection = DriverManager.getConnection(url,username,password);
                while(true){
                    System.out.println();
                    System.out.println("HOTEL MANAGEMENT");
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("1. Reserve a room");
                    System.out.println("2. View Reservations");
                    System.out.println("3. Get Room");
                    System.out.println("4. Update Reservation");
                    System.out.println("5. Delete Reservation");
                    System.out.println("0. Exit");
                    int choice = scanner.nextInt();
                    switch(choice){
                        case 1:
                            reserveRoom(connection,scanner);
                            break;
                        case 2:
                            viewReservation(connection,scanner);
                            break;
                        case 3:
                            getRoomNumber(connection,scanner);
                            break;
                        case 4:
                            updateReservation(connection,scanner);
                            break;
                        case 5:
                            deleteReservation(connection,scanner);
                            break;
                        case 0:
                            exit();
                            scanner.close();
                            return ;
                        default:
                            System.out.println("Invalid choice. Try again");
                    }
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    public static void reserveRoom(Connection connection,Scanner scanner){
        try{
            System.out.println("enter guest name");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("enter room number");
            int roomNumber = scanner.nextInt();
            System.out.println("enter contact number");
            String contactNumber = scanner.next();
            String sql = "INSERT INTO reservations (guest_name, room_num, contact_num) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";
            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows >0){
                    System.out.println("Reservation successfull");
                }else{
                    System.out.println("Reservation failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void viewReservation(Connection connection,Scanner scanner) throws SQLException{
        String sql = "SELECT reservation_id,guest_name,room_num,contact_num,reservation_date FROM reservations";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while(resultSet.next()){
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_num");
                String contactNumber = resultSet.getString("contact_num");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }
        }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
    }
    private static void getRoomNumber(Connection connection, Scanner scanner) throws SQLException{
            try {
                System.out.println("enter reservation id");
                int reservationID = scanner.nextInt();
                System.out.println("enter guest name");
                String guestName = scanner.next();
                String sql = "SELECT room_num FROM reservations " +
                        "WHERE reservation_id = " + reservationID +
                        " AND guest_name = '" + guestName + "'";
                try(Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)){
                    if(resultSet.next()){
                        int roomNumber = resultSet.getInt("room_num");
                        System.out.println("Room number for reservation id " + reservationID+" and guest "+ guestName +" is: "+roomNumber);
                    }else{
                        System.out.println("reservation id not found");
                    }
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
    }
    private static void updateReservation(Connection connection,Scanner scanner){
            try{
                System.out.print("Enter reservation ID to update");
                int reservationId = scanner.nextInt();
                scanner.nextLine();
                if(!reservationExists(connection,reservationId)){
                    System.out.println("Reservation not found for the given ID");
                    return ;
                }
                System.out.println("enter new guest name");
                String newGuestName = scanner.nextLine();
                System.out.println("Enter new room number: ");
                int newRoomNumber = scanner.nextInt();
                System.out.println("enter new contact number: ");
                String newContactNumber = scanner.next();
                String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                        "room_num = " + newRoomNumber + ", " +
                        "contact_num = '" + newContactNumber + "' " +
                        "WHERE reservation_id = " + reservationId;
                try(Statement statement = connection.createStatement()){
                    int affectedRows = statement.executeUpdate(sql);
                    if(affectedRows >0){
                        System.out.println("Reservation updated successfully!");
                    }else{
                        System.out.println("Reservation update failed");
                    }
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
    }
    private static boolean reservationExists(Connection connection,int reservationId){
            try{
                String sql = "SELECT reservation_id FROM reservations WHERE reservation_id =  "+reservationId;
                try(Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)){
                    return resultSet.next();
                }
            }catch(SQLException e){
                e.printStackTrace();
                return  false;
            }
    }
    private static void deleteReservation(Connection connection,Scanner scanner){
            try{
                System.out.println("Enter reservation ID to delete: ");
                int reservationID = scanner.nextInt();
                if(!reservationExists(connection,reservationID)){
                    System.out.println("Reservation not found for the given ID");
                    return;
                }
                String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationID;
                try(Statement statement = connection.createStatement()){
                    int affectedRows = statement.executeUpdate(sql);
                    if(affectedRows > 0){
                        System.out.println("Reservation deleted successfully!");
                    }else{
                        System.out.println("Reservation deletion failed");
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
    }
    public static void exit() throws InterruptedException{
        System.out.println("Existing System");
        int i=5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using Hotel Reservation System !!! ");
    }
}
