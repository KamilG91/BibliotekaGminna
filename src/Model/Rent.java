package Model;


import java.sql.Date;

public class Rent {
    private int rentId;
    private int cId;
    private int bId;
    private Date rentDate;
    private Date agreedReturnDate;
    private Date returnDate;


    public Rent() {}
    /**
     * When creating new Rent, by default the return date is null.
     * @param id of new Book
     * @param clientID of new Book
     * @param bookID of new Book
     * @param rentDate of new Book
     * @param agreedReturnDate of new Book
     */

    public Rent(int id, int clientID, int bookID, Date rentDate, Date agreedReturnDate) {
        this.rentId = id;
        this.cId = clientID;
        this.bId = bookID;
        this.rentDate = rentDate;
        this.agreedReturnDate = agreedReturnDate;
        this.returnDate = null;
    }

    /**
     * Used when getting data from Database
     * @param id of new Book
     * @param clientID of new Book
     * @param bookID of new Book
     * @param rentDate of new Book
     * @param agreedReturnDate of new Book
     * @param returnDate of new Book
     */
    public Rent(int id, int clientID, int bookID, Date rentDate, Date agreedReturnDate, Date returnDate) {
        this.rentId = id;
        this.cId = clientID;
        this.bId = bookID;
        this.rentDate = rentDate;
        this.agreedReturnDate = agreedReturnDate;
        this.returnDate = returnDate;
    }
    public void setRentId(int id){
        rentId = id;
    }
    public int getRentId() {
        return rentId;
    }

    public int getcId() {
        return cId;
    }

    public int getbId() {
        return bId;
    }

    public Date getRentDate() {
        return rentDate;
    }

    public Date getAgreedReturnDate() {
        return agreedReturnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    @Override
    public String toString() {
        return returnDate == null ?
                rentId + ". ClientId : " + cId +  ", BookId : " + bId + ". Rent started : " + rentDate + ", agreedReturnDate : " + agreedReturnDate
                : rentId + ". ClientId : " + cId +  ", BookId : " + bId + ". Rent started : " + rentDate + ", agreedReturnDate : " + agreedReturnDate + " returned : " + returnDate;
    }

    public String toShortDescString() {
        return "RentID : " + this.rentId + ", ClientID : " + this.cId + ", BookID : " + this.bId + ". Agreed Return date : " + this.agreedReturnDate;
    }

    public String toOnlyClientBookInfo() {
        return "RentID : " + this.rentId + ", ClientID : " + this.cId + ", BookID : " + this.bId;
    }
}
