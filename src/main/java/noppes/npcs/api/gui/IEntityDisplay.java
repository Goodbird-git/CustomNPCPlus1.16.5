package noppes.npcs.api.gui;

import noppes.npcs.api.entity.IEntity;

public interface IEntityDisplay extends ICustomGuiComponent {
    IEntity getEntity();

    IEntityDisplay setEntity(IEntity entity);

    int getRotation();
    IEntityDisplay setRotation(int deg);

    boolean isFollowingCursor();
    IEntityDisplay setFollowingCursor(boolean state);

    float getScale();
    IEntityDisplay setScale(float scaleFactor);

    int getWidth();
    int getHeight();
    IEntityDisplay setHoverBox(int width, int height);
}
