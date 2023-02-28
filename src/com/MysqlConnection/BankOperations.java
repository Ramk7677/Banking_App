package com.MysqlConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class BankOperations {

    public static void main(String[] args) throws NumberFormatException, IOException, SQLException, ParseException {
        
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("==============================================================================");
        System.out.println("=============================== WELCOME TO ABC BANK ==========================");
        System.out.println("==============================================================================");
        

        System.out.println("1  --->   Customer");
        System.out.println("2  --->   Admin");
        System.out.println("==============================================================================");
        System.out.print("\t\t Enter your choice:"); //escape sequence \t,\n
        int choice=Integer.parseInt(br.readLine());
        
        
        if(choice==1)
        {
            System.out.println("==============================================================================");
            System.out.println("==========================  ENTER LOGIN DETAILS ==============================");
            System.out.print("\t\t Enter your username:");
            String userName=br.readLine();
            System.out.print("\t\t Enter your password:");
            String userPassword=br.readLine();
            System.out.println("==============================================================================");
            
            Connection conn=MysqlConnection.getConnection();
            PreparedStatement ps=conn.prepareStatement("select * from accounts where userName=?");
            ps.setString(1,userName);
            ResultSet result=ps.executeQuery();
            String password=null;
                
                while(result.next())
                {
                    password=result.getString("userPassword");
                }
            
            
            if(userPassword.equals(password))
            {
            
                System.out.println("You have successfully logged in!!");
                
                boolean login=true;
                do
                {
                
                System.out.println("==============================================================================");
                System.out.println("==========================  WELCOME " + userName.toUpperCase() + " ==============================");
                System.out.println("==============================================================================");
                System.out.println("1  --->   Deposit");
                System.out.println("2  --->   Withdraw");
                System.out.println("3  --->   Fund Transfer");
                System.out.println("4  --->   Balance Check");
                System.out.println("5  --->   Acc. Info Check");
                System.out.println("6  --->   Change Password");
                System.out.println("7  --->   Transaction History");
                System.out.println("8  --->   Exit / Logout");
                System.out.println("==============================================================================");               
                System.out.print("\t\t Enter your choice:"); 
                int operationNumber=Integer.parseInt(br.readLine());
                System.out.println("==============================================================================");
                
                
                String status=null;
                
                switch(operationNumber)
                {
                case 1: System.out.println("Enter deposit amount:");
                double depositAmount=Double.parseDouble(br.readLine());
                
                if(depositAmount>0)
                {
                    conn=MysqlConnection.getConnection();
                    ps=conn.prepareStatement("select * from accounts where userName=?");
                    ps.setString(1, userName);
                    result=ps.executeQuery();
                    
                    double balance=0.0;
                    long accId=0;
                    while(result.next())
                    {
                        balance=result.getDouble("accBalance");
                        accId=result.getLong("accId");
                    }
                    
                    balance=balance+depositAmount;
                    
                    ps=conn.prepareStatement("update accounts set accBalance=? where userName=?");
                    ps.setDouble(1, balance);
                    ps.setString(2, userName);
                    
                    if(ps.executeUpdate()>0)
                    {
                        ps=conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String transactionId="TN"+timestamp.getTime(); //TN3243432432423
                        ps.setString(1, transactionId);
                        ps.setDouble(2, depositAmount);
                        ps.setDate(3, new Date(System.currentTimeMillis()));
                        ps.setString(4, "deposit");
                        ps.setLong(5,accId);
                        ps.setLong(6,accId);
                        
                        ps.executeUpdate();

                        
                        System.out.println("Balance Updated!!");
                        System.out.println("New Balance: "+balance);
                    }
                    else
                    {
                        System.out.println("Something went wrong!!");
                    }
                    
                }
                
                System.out.println("Do you want to continue??(Y/N)");
                 status=br.readLine();
                
                if(status.equals("n") || status.equals("N"))
                {
                    login=false;
                }
                
                break;
        case 2:  System.out.println("Enter Withdrawal amount:");
         double withdrawalAmount=Double.parseDouble(br.readLine());
         if(withdrawalAmount>0)
         {
            conn=MysqlConnection.getConnection();
            ps=conn.prepareStatement("select * from accounts where userName=?");
            ps.setString(1, userName);
            result=ps.executeQuery();
            
            double balance=0.0;
            long accId=0;
            while(result.next())
            {
                balance=result.getDouble("accBalance");
                accId=result.getLong("accId");
            }
            
            
            if(balance>withdrawalAmount)
            {
                balance=balance-withdrawalAmount;
                ps=conn.prepareStatement("update accounts set accBalance=? where userName=?");
                ps.setDouble(1, balance);
                ps.setString(2, userName);
                
                if(ps.executeUpdate()>0)
                {
                    ps=conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String transactionId="TN"+timestamp.getTime(); //TN3243432432423
                    ps.setString(1, transactionId);
                    ps.setDouble(2, withdrawalAmount);
                    ps.setDate(3, new Date(System.currentTimeMillis()));
                    ps.setString(4, "withdraw");
                    ps.setLong(5,accId);
                    ps.setLong(6,accId);
                    
                    ps.executeUpdate();
                    
                    System.out.println("Balance Updated!!");
                    System.out.println("New Balance: "+balance);
                }
                else
                {
                    System.out.println("Something went wrong!!");
                }
            }
            else
            {
                System.out.println("Insufficient Balance!!");
            }

         }
         System.out.println("Do you want to continue??(Y/N)");
         status=br.readLine();
            
            if(status.equals("n") || status.equals("N"))
            {
                login=false;
            }
            
            break;
            
            
      case 3: System.out.println("Please enter the receiver account Id:");
        long rcveId=Long.parseLong(br.readLine());
        
        System.out.println("Enter the amount:");
        double amount=Double.parseDouble(br.readLine());
        
        conn=MysqlConnection.getConnection();
        
        long receiverId=0;
        
        ps=conn.prepareStatement("select * from accounts where accId=?");
        ps.setLong(1, rcveId);
        result=ps.executeQuery();
        
        while(result.next())
        {
            receiverId=result.getLong("accId");
        }
        
        double availableBalance=0.0;
        long senderId=0;
        ps=conn.prepareStatement("select accBalance,accId from accounts where userName=?");
        ps.setString(1, userName);
        result=ps.executeQuery();
        
        while(result.next())
        {
            availableBalance=result.getDouble("accBalance");
            senderId=result.getLong("accId");
        }
        
        if(receiverId==0)
        {
            System.out.println("==============================================================================");               
            System.out.println("Wrong receiver id!!");
            System.out.println("==============================================================================");               

        }
        else if(availableBalance==0 || availableBalance<amount)
        {
            System.out.println("==============================================================================");               
            System.out.println("Insufficient account balance!!");
            System.out.println("==============================================================================");               

        }
        else
        {
            availableBalance=availableBalance-amount;
            ps=conn.prepareStatement("update accounts set accBalance=? where userName=?");
            ps.setDouble(1, availableBalance);
            ps.setString(2, userName);
            
            if(ps.executeUpdate()>0)
            {
                ps=conn.prepareStatement("select accBalance from accounts where accId=?");
                ps.setLong(1, rcveId);
                double rcvBalance=0.0;
                result=ps.executeQuery();
                while(result.next())
                {
                    rcvBalance=result.getDouble("accBalance");
                }
                
                rcvBalance=rcvBalance + amount;
                
                ps=conn.prepareStatement("update accounts set accBalance=? where accId=?");
                ps.setDouble(1, rcvBalance);
                ps.setLong(2, receiverId);
                
                if(ps.executeUpdate()>0)
                {
                     ps=conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String transactionId="TN"+timestamp.getTime(); //TN3243432432423
                        ps.setString(1, transactionId);
                        ps.setDouble(2, amount);
                        ps.setDate(3, new Date(System.currentTimeMillis()));
                        ps.setString(4, "fund transfer");
                        ps.setLong(5,senderId);
                        ps.setLong(6,rcveId);
                        
                        ps.executeUpdate();
                    System.out.println("==============================================================================");               
                    System.out.println("Transaction Completed!!");
                    System.out.println("==============================================================================");               

                }
                else
                {
                    System.out.println("==============================================================================");               
                    System.out.println("Transaction Failed!!");
                    System.out.println("==============================================================================");               

                }
                
            }
            
            
        }
        System.out.println("Do you want to continue??(Y/N)");
         status=br.readLine();
        
        if(status.equals("n") || status.equals("N"))
        {
            login=false;
        }
        
        break;                            

                            
                        
                        
                        
                    case 4: conn=MysqlConnection.getConnection();
                            ps=conn.prepareStatement("select accBalance from accounts where userName=?");
                            ps.setString(1, userName);
                            result=ps.executeQuery();
                            
                            double balance=0.0;
                            while(result.next())
                            {
                                balance=result.getDouble("accBalance");
                            }
                            System.out.println("==============================================================================");               
                            System.out.println("Current Available Balance:"+balance);
                            System.out.println("==============================================================================");               

                             System.out.println("Do you want to continue??(Y/N)");
                             status=br.readLine();
                                
                                if(status.equals("n") || status.equals("N"))
                                {
                                    login=false;
                                }
                                
                                break;
                    case 5: ps=conn.prepareStatement("select * from accounts where userName=?");
                            ps.setString(1, userName);
                            result=ps.executeQuery();
                            
                            while(result.next())
                            {
                                System.out.println("==============================================================================");               
                                System.out.println("Account Details:");
                                System.out.println("==============================================================================");               
                                System.out.println("Account Id: "+result.getLong("accId"));
                                System.out.println("Account Name:"+result.getString("accName"));
                                System.out.println("Account Type: "+result.getString("accType"));
                                System.out.println("Gender: "+result.getString("gender"));
                                System.out.println("Date of Birth :"+result.getDate("dob"));
                                System.out.println("Account Balance:"+result.getDouble("accBalance"));
                                System.out.println("Account Id:"+result.getLong("accId"));
                                System.out.println("Email: "+result.getString("email"));
                                System.out.println("Phone:"+result.getLong("phone"));
                                System.out.println("==============================================================================");               
                                

                            }
                            
                            System.out.println("Do you want to continue??(Y/N)");
                             status=br.readLine();
                            
                            if(status.equals("n") || status.equals("N"))
                            {
                                login=false;
                            }
                            break;
                            
                  case 6: System.out.println("Please enter the existing password: ");
                    String existingPassword=br.readLine();
                    
                    System.out.println("Set new Password:");
                    String newPassword=br.readLine();
                    
                    System.out.println("Retype new password:");
                    String retypePassword=br.readLine();
                    
                    
                    ps=conn.prepareStatement("select userPassword from accounts where userName=?");
                    ps.setString(1, userName);
                    
                    result=ps.executeQuery();
                    String accountPassword=null;
                    while(result.next())
                    {
                        accountPassword=result.getString("userPassword");
                    }
                    
                    if(accountPassword.equals(existingPassword))
                    {
                        if(newPassword.equals(retypePassword))
                        {
                            ps=conn.prepareStatement("update accounts set userPassword=? where userName=?");
                            ps.setString(1, newPassword);
                            ps.setString(2, userName);
                            
                            if(ps.executeUpdate()>0)
                            {
                                System.out.println("==============================================================================");               
                                System.out.println("Passowrd Changed!!");
                                System.out.println("==============================================================================");               
                                
                            }
                            
                            else
                            {
                                System.out.println("==============================================================================");               
                                System.out.println("Error in password change!!");
                                System.out.println("==============================================================================");               
                                
                            }
                        }
                        else
                        {
                            System.out.println("==============================================================================");               
                            System.out.println("Set new password and retype password must be same!!");
                            System.out.println("==============================================================================");               
                            
                        }
                    } 
                    else
                    {
                        System.out.println("==============================================================================");               
                        System.out.println("Please enter correct existing password!!");
                        System.out.println("==============================================================================");               
                        
            
                    }
                    
                    System.out.println("Do you want to continue??(Y/N)");
                     status=br.readLine();
                    
                    if(status.equals("n") || status.equals("N"))
                    {
                        login=false;
                    }
                    break;   
                    
                    
                  case 7: ps=conn.prepareStatement("select * from accounts where userName=?");
                  ps.setString(1, userName);
                  
                  result=ps.executeQuery();
                  long accId=0;
                  while(result.next())
                  {
                      accId=result.getLong("accId");
                  }
                  if(accId!=0)
                  {
                      ps=conn.prepareStatement("select * from transactions where senderAccountId=?");
                      ps.setLong(1, accId);
                      
                      result=ps.executeQuery();
                      System.out.println("==============================================================================");   
                      System.out.println("TransactionId \t Amount \t Date \t Type ");
                      System.out.println("==============================================================================");   
                      while(result.next())
                      {
                          System.out.println(result.getString("transactionId")+"\t"+result.getDouble("transactionAmount")+"\t"+result.getDate("transactiondate")+"\t"+result.getString("transactionType"));
                      }
                      System.out.println("==============================================================================");   
                  }
                  System.out.println("Do you want to continue??(Y/N)");
                  status=br.readLine();
                  System.out.println("==============================================================================");               

                  if(status.equals("n") || status.equals("N"))
                  {
                      login=false;
                  }
                  break;
          case 8:  login=false;
                   break;

                }
                
                
                
            }
                while(login);
                System.out.println("Bye.");
                System.out.println("Have a nice day!!");
            }
            else
            {
                System.out.println("Wrong username/password!!");
            }
            
            
        }
        else if(choice==2)
        {
            System.out.println("==============================================================================");
            System.out.println("==========================  ENTER LOGIN DETAILS ==============================");
            System.out.print("\t\t Enter your username:");
            String userName=br.readLine();
            System.out.print("\t\t Enter your password:");
            String userPassword=br.readLine();
            System.out.println("==============================================================================");
            
            Connection conn=MysqlConnection.getConnection();
            PreparedStatement ps=conn.prepareStatement("select * from admin where userName=?");
            ps.setString(1,userName);
            ResultSet result=ps.executeQuery();
            String password=null;
                
                while(result.next())
                {
                    password=result.getString("password");
                }
            
            
            if(userPassword.equals(password))
            {
            
                System.out.println("You have successfully logged in!!");
                
                boolean login=true;
            do
            {
            
            System.out.println("==============================================================================");
            System.out.println("==========================  WELCOME " + userName.toUpperCase() + " ==============================");
            System.out.println("==============================================================================");
            System.out.println("1  --->   Open new Account");
            System.out.println("2  --->   Close account");
            System.out.println("3  --->   View transactions");
            System.out.println("4  --->   Exit / Logout");
            System.out.println("==============================================================================");               
            System.out.print("\t\t Enter your choice:"); 
            int operationNumber=Integer.parseInt(br.readLine());
            System.out.println("==============================================================================");
            
            
            String status=null;
            switch(operationNumber)
            {
                case 1: System.out.println("Enter Customer full name:");
                        String name=br.readLine();
                        
                        System.out.println("Enter user name");
                        String uname=br.readLine();
                        
                        System.out.println("Enter gender:");
                        String gender=br.readLine();
                        
                        System.out.println("Enter date of birth:(dd/MM/YYYY)");
                        String dob=br.readLine();
                        
                        System.out.println("Enter email:");
                        String email=br.readLine();
                        
                        System.out.println("Enter phone number:");
                        long phone=Long.parseLong(br.readLine());
                        
                        
                        System.out.println("Set account password");
                        String accPassword=br.readLine();
                        
                        System.out.println("Retype password:");
                        String rePassword=br.readLine();
                        
                        System.out.println("Set account Id:");
                        long accId=Long.parseLong(br.readLine());
                        
                        System.out.println("Account type:");
                        String accType=br.readLine();
                        
                        System.out.println("Enter initial balance:");
                        double balance=Double.parseDouble(br.readLine());
                        
                        System.out.println("Enter IFSC code");
                        String ifsc=br.readLine();
                        
                        
                        ps=conn.prepareStatement("insert into accounts values(?,?,?,?,?,?,?,?,?,?,?)");
                        ps.setLong(1, accId);
                        ps.setString(2,name);
                        ps.setString(3, uname);
                        ps.setString(4, accPassword);
                        ps.setString(5, accType);
                        ps.setString(6, gender);
                        
                        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/YYYY");
                        java.util.Date utilDate=formatter.parse(dob);
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                        ps.setDate(7,sqlDate);
                        
                        ps.setDouble(8, balance);
                        ps.setString(9, email);
                        ps.setLong(10, phone);
                        ps.setString(11, ifsc);
                        
                        if(ps.executeUpdate()>0)
                        {
                            System.out.println("==============================================================================");               
                            System.out.println("New account created successfully!!");
                            System.out.println("==============================================================================");               
                        
                        }
                        else
                        {
                            System.out.println("==============================================================================");               
                            System.out.println("Problem in account creation!!");
                            System.out.println("==============================================================================");               
                        
                        }
                        

                        System.out.println("Do you want to continue??(Y/N)");
                        status=br.readLine();
                        
                        if(status.equals("n") || status.equals("N"))
                        {
                            login=false;
                        }
                        
                        break;
                case 2: System.out.println("Enter account Id:");    
                        accId=Long.parseLong(br.readLine());
                        
                        ps=conn.prepareStatement("delete from accounts where accId =?");
                        ps.setLong(1, accId);
                        
                        if(ps.executeUpdate()>0)
                        {
                            System.out.println("==============================================================================");               
                            System.out.println("Account closed successfully!!");
                            System.out.println("==============================================================================");               
                        
                        }
                        else
                        {
                            System.out.println("==============================================================================");               
                            System.out.println("Account id does not exist!!");
                            System.out.println("==============================================================================");               
                        
                    
                        }
                        System.out.println("Do you want to continue??(Y/N)");
                        status=br.readLine();
                        
                        if(status.equals("n") || status.equals("N"))
                        {
                            login=false;
                        }
                        
                     break;
                case 3: ps=conn.prepareStatement("select * from transactions");
                        result=ps.executeQuery();
                        System.out.println("==============================================================================");   
                        System.out.println("TransactionId \t Amount \t Date \t Type ");
                        System.out.println("==============================================================================");   
                        while(result.next())
                        {
                            System.out.println(result.getString("transactionId")+"\t"+result.getDouble("transactionAmount")+"\t"+result.getDate("transactiondate")+"\t"+result.getString("transactionType"));
                        }
                        System.out.println("==============================================================================");    
                        System.out.println("Do you want to continue??(Y/N)");
                        status=br.readLine();
                        
                        if(status.equals("n") || status.equals("N"))
                        {
                            login=false;
                        }
                        break;  
                case 4: login=false;
                        break;
    
                default:System.out.println("==============================================================================");    
                        System.out.println("Wrong Input!!");    
                        System.out.println("==============================================================================");    
                        System.out.println("Do you want to continue??(Y/N)");
                        status=br.readLine();
                        
                        if(status.equals("n") || status.equals("N"))
                        {
                            login=false;
                        }   
                        break;
                        
                        
            }
            
            
            }
            while(login);
            System.out.println("==============================================================================");               
            System.out.println("Bye.");
            System.out.println("Have a nice day!!");
            System.out.println("==============================================================================");               

        }
        else if(password==null)
        {
            System.out.println("==============================================================================");               
            System.out.println("Username does not exist!!");
            System.out.println("==============================================================================");               

        }
        else
        {
            System.out.println("==============================================================================");               
            System.out.println("Wrong password!!");
            System.out.println("==============================================================================");               

        }
    

    }
        
        else
        {
            System.out.println("Wrong Choice..");
        }
        
        
        
        
        

    }

}

