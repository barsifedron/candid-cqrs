package com.barsifedron.candid.cqrs.springboot.sample.writeside.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandToLog;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PleaseDoSomethingCommand implements Command<String>, CommandToLog {

    @Min(3)
    @Max(20)
    @NotNull
    public final Integer paramAboveZero;

    @Min(1)
    @Max(10)
    @NotNull
    public final Integer paramBetweenOneAndTen;

    /**
     * Not a big fan of lombok but there really is no shame in using it for this
     */
    public PleaseDoSomethingCommand(Integer paramAboveZero, Integer paramBetweenOneAndTen) {
        this.paramAboveZero = paramAboveZero;
        this.paramBetweenOneAndTen = paramBetweenOneAndTen;
    }


    /**
     * The logging intention expressed with the CommandToLog interface will result in a call the toString method.
     * <p>
     * Not a big fan of lombok but there really is no shame in using it for this
     */
    @Override
    public String toString() {
        return "PleaseDoSomethingCommand{" +
                "paramAboveZero=" + paramAboveZero +
                ", paramBetweenOneAndTen=" + paramBetweenOneAndTen +
                '}';
    }
}
