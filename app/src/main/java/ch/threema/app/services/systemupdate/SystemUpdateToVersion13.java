/*  _____ _
 * |_   _| |_  _ _ ___ ___ _ __  __ _
 *   | | | ' \| '_/ -_) -_) '  \/ _` |_
 *   |_| |_||_|_| \___\___|_|_|_\__,_(_)
 *
 * Threema for Android
 * Copyright (c) 2014-2022 Threema GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.threema.app.services.systemupdate;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Arrays;

import ch.threema.app.ThreemaApplication;
import ch.threema.app.collections.Functional;
import ch.threema.app.collections.IPredicateNonNull;
import ch.threema.app.managers.ServiceManager;
import ch.threema.app.services.GroupService;
import ch.threema.app.services.UpdateSystemService;
import ch.threema.storage.models.GroupModel;

public class SystemUpdateToVersion13 implements UpdateSystemService.SystemUpdate {
	private final SQLiteDatabase sqLiteDatabase;

	public SystemUpdateToVersion13( SQLiteDatabase sqLiteDatabase) {
		this.sqLiteDatabase = sqLiteDatabase;
	}

	@Override
	public boolean runDirectly() {
		//update db first
		String[] messageTableColumnNames = sqLiteDatabase.rawQuery("SELECT * FROM group_member LIMIT 0", null).getColumnNames();

		boolean hasField = Functional.select(Arrays.asList(messageTableColumnNames), new IPredicateNonNull<String>() {
			@Override
			public boolean apply(@NonNull String type) {
				return type.equals("color");
			}
		}) != null;


		if(!hasField) {
			sqLiteDatabase.rawExecSQL("ALTER TABLE group_member ADD COLUMN color INT DEFAULT 0");
		}

		return true;
	}

	@Override
	public boolean runASync() {
		//make a manually sync
		ServiceManager serviceManager = ThreemaApplication.getServiceManager();
		if(serviceManager != null) {
			try {
				GroupService groupService = serviceManager.getGroupService();
				if(groupService != null) {
					for(GroupModel groupModel: groupService.getAll()) {
						if(groupModel != null) {
							groupService.rebuildColors(groupModel);
						}
					}
				}
			} catch (Exception e) {
				//do nothing
			}
		}
		return true;
	}

	@Override
	public String getText() {
		return "version 13";
	}
}
