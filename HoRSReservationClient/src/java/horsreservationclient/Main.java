/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author PP42
 */
public class Main {

    @EJB
    private static GuestSessionBeanRemote GuestSessionBean;
    @EJB
    private static RoomSessionBeanRemote roomSessionBean;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBean;
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(GuestSessionBean, roomSessionBean, roomTypeSessionBean, roomRateSessionBean, reservationSessionBean);
        mainApp.runApp();
        
        //hiiiiiiii
    }

}
