/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Embeddable
public class RoomConfig implements Comparable<RoomConfig>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer ranking;

    @Column(nullable = false, length = BossHelper.NAME_LENGTH, unique = true)
    @NotNull
    @Size(min = 5, max = BossHelper.NAME_LENGTH)
    private String name;

    @Column(nullable = false)
    @NotNull
    private String description;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Long mySize;

    @Column(nullable = false)
    @NotNull
    private String bed;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer capacity;

    public RoomConfig() {

    }

    public RoomConfig(Integer ranking, String name, String description, Long mySize, String bed, Integer capacity) {
        this.ranking = ranking;
        this.name = name;
        this.description = description;
        this.mySize = mySize;
        this.bed = bed;
        this.capacity = capacity;
    }

    @Override
    public int compareTo(RoomConfig o) {
        return this.ranking - o.ranking;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMySize() {
        return mySize;
    }

    public void setMySize(Long mySize) {
        this.mySize = mySize;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
