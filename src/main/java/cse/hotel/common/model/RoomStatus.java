package cse.hotel.common.model;

import java.io.Serializable;

public enum RoomStatus implements Serializable { 
    AVAILABLE,  // 빈 객실
    OCCUPIED,   // 점유중
    CLEANING,   // 청소중
    RESERVED    // 예약됨
}