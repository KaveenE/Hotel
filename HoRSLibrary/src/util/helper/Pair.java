/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.io.Serializable;
import javax.enterprise.inject.se.SeContainer;

/**
 *
 * @author enkav
 */
//Represents a grid's row and col number
public class Pair<E, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final E element1;
    private final V element2;

    public Pair(E one, V two) {
        element1 = one;
        element2 = two;
    }

    public E getFirst() {
        return element1;
    }

    public V getSecond() {
        return element2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element1 == null) ? 0 : element1.hashCode());
        result = prime * result + ((element2 == null) ? 0 : element2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pair<E, V> other = (Pair<E, V>) obj;
        if (element1 == null) {
            if (other.element1 != null) {
                return false;
            }
        } else if (!element1.equals(other.element1)) {
            return false;
        }
        if (element2 == null) {
            if (other.element2 != null) {
                return false;
            }
        } else if (!element2.equals(other.element2)) {
            return false;
        }
        return true;
    }

}
