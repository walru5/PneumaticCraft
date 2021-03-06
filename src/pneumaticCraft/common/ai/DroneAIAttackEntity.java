package pneumaticCraft.common.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.item.ItemMachineUpgrade;

public class DroneAIAttackEntity extends EntityAIAttackOnCollide{
    private final EntityDrone attacker;
    private final boolean isRanged;
    private final double rangedAttackRange;

    public DroneAIAttackEntity(EntityDrone attacker, double speed, boolean p_i1636_4_){
        super(attacker, speed, p_i1636_4_);
        this.attacker = attacker;
        isRanged = attacker.hasMinigun();
        rangedAttackRange = 16 + Math.min(16, ((IDroneBase)attacker).getUpgrades(ItemMachineUpgrade.UPGRADE_RANGE));
    }

    @Override
    public boolean shouldExecute(){
        if(isRanged && attacker.getAmmo() == null) return false;
        return super.shouldExecute();
    }

    @Override
    public boolean continueExecuting(){
        if(isRanged) {
            EntityLivingBase entitylivingbase = attacker.getAttackTarget();
            if(entitylivingbase == null) return false;
            double dist = attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
            if(attacker.getAmmo() == null) return false;
            if(dist < Math.pow(rangedAttackRange, 2) && attacker.getEntitySenses().canSee(entitylivingbase)) return true;
        }
        return super.continueExecuting();
    }

    @Override
    public void resetTask(){

    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask(){
        boolean needingSuper = true;
        if(isRanged) {
            EntityLivingBase entitylivingbase = attacker.getAttackTarget();
            double dist = attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
            if(dist < Math.pow(rangedAttackRange, 2) && attacker.getEntitySenses().canSee(entitylivingbase)) {
                attacker.tryFireMinigun(entitylivingbase);
                needingSuper = false;
                if(dist < Math.pow(rangedAttackRange - 4, 2)) {
                    attacker.getNavigator().clearPathEntity();
                }
            }
        }
        if(needingSuper) super.updateTask();
    }
}
