package com.tinygame.model;

import lombok.Data;


@Data
public class MoveState {

   public float fromPosX ;
    // 起始位置 Y
   public float fromPosY ;
    // 移动到位置 X
   public float toPosX ;
    // 移动到位置 Y
   public float toPosY ;
    // 启程时间戳
   public long startTime ;


}
