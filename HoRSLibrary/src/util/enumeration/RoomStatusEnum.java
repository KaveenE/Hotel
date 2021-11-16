/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.enumeration;

/**
 *
 * @author PP42
 */
public enum RoomStatusEnum {

    /*
    * Available = Not under maintenance. It is in inventory and can be reserved if and only if got no clashes with Room schedule.
    * Unavailble = Under maintenance. Not in inventory at all
     */
    AVAILABLE,
    UNAVAILABLE,

}
