package net.simplyrin.annihilation.rp.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.simplyrin.annihilation.rp.Main;

/**
 * Created by SimplyRin on 2018/07/14.
 *
 * Copyright (C) 2018 SimplyRin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class CommandResetRP extends CommandBase {

	private Main instance;

	public CommandResetRP(Main instance) {
		this.instance = instance;
	}

	@Override
	public String getCommandName() {
		return "annirp";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("annihilationrp", "anni-rp");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/annirp";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
	}

}
