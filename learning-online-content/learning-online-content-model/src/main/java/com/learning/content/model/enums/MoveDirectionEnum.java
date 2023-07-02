package com.learning.content.model.enums;

/**
 * @author HH
 * @version 1.0
 * @description 排序移动方向枚举类
 * @date 2023/7/2 10:50
 **/
public enum MoveDirectionEnum {
    Move_Down("movedown", 1),
    Move_UP("moveup", -1);
    // 移动方向
    private String moveDirection;
    // 移动长度
    private int step;

    public static int MoveDirectionEnumOf(String moveDirection) {
        for (MoveDirectionEnum moveDirectionEnum : MoveDirectionEnum.values()) {
            if (moveDirectionEnum.moveDirection.equals(moveDirection)) {
                return moveDirectionEnum.step;
            }
        }
        return 0;
    }

    MoveDirectionEnum(String moveDirection, int step) {
        this.moveDirection = moveDirection;
        this.step = step;
    }
}
