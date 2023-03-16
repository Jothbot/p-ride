package net.jothb.pride.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.server.command.RideCommand.class)
public class RideCommand {
    private static final Dynamic2CommandExceptionType ALREADY_RIDING_EXCEPTION = new Dynamic2CommandExceptionType((rider, vehicle) -> {
        return Text.translatable("commands.ride.already_riding", rider, vehicle);
    });
    private static final Dynamic2CommandExceptionType GENERIC_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((rider, vehicle) -> {
        return Text.translatable("commands.ride.mount.failure.generic", rider, vehicle);
    });
    private static final SimpleCommandExceptionType RIDE_LOOP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.loop"));

    private static final SimpleCommandExceptionType WRONG_DIMENSION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.wrong_dimension"));

    /**
     * @author JothB
     * @reason This is a very quick and dirty overwrite of the ride command to allow Players as mountable entities.
     * If you read this, and you aren't me, close this immediately and delete this obscenity from your computer. Sterilize it for good measure too
     */
    @Overwrite
    private static int executeMount(ServerCommandSource source, Entity rider, Entity vehicle) throws CommandSyntaxException {
        Entity entity = rider.getVehicle();
        if (entity != null) {
            throw ALREADY_RIDING_EXCEPTION.create(rider.getDisplayName(), entity.getDisplayName());
        } else if (vehicle.getType() == EntityType.PLAYER) {
            ride(rider.getWorld(),rider,vehicle);
            return 1;
        } else if (rider.streamSelfAndPassengers().anyMatch((passenger) -> {
            return passenger == vehicle;
        })) {
            throw RIDE_LOOP_EXCEPTION.create();
        } else if (rider.getWorld() != vehicle.getWorld()) {
            throw WRONG_DIMENSION_EXCEPTION.create();
        } else if (!rider.startRiding(vehicle, true)) {
            throw GENERIC_FAILURE_EXCEPTION.create(rider.getDisplayName(), vehicle.getDisplayName());
        } else {
            source.sendFeedback(Text.translatable("commands.ride.mount.success", rider.getDisplayName(), vehicle.getDisplayName()), true);
            return 1;
        }
    }

    private static boolean ride(World world,Entity passenger,Entity player){
        if (!world.isClient){
            while (passenger.getFirstPassenger() != null)
            {
                passenger = passenger.getFirstPassenger();
            }
            passenger.startRiding(player);

            ((ServerPlayerEntity) player).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(player));
        }

        return false;
    }

}