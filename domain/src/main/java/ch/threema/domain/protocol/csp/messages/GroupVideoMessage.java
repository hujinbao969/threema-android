/*  _____ _
 * |_   _| |_  _ _ ___ ___ _ __  __ _
 *   | | | ' \| '_/ -_) -_) '  \/ _` |_
 *   |_| |_||_|_| \___\___|_|_|_\__,_(_)
 *
 * Threema for Android
 * Copyright (c) 2013-2022 Threema GmbH
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

package ch.threema.domain.protocol.csp.messages;

import org.apache.commons.io.EndianUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import ch.threema.domain.protocol.csp.ProtocolDefines;

/**
 * A group message that has a video including thumbnail (stored on the blob server) as its content.
 *
 * The contents are referenced by the {@code videoBlobId}/{@code thumbnailBlobId},
 * the {@code videoSize}/{@code thumbnailSize} in bytes, and the {@code encryptionKey}
 * to be used when decrypting the video blob.
 *
 * The thumbnail uses the same key, the nonces are as follows:
 *
 * Video:     0x000000000000000000000000000000000000000000000001
 * Thumbnail: 0x000000000000000000000000000000000000000000000002
 */
public class GroupVideoMessage extends AbstractGroupMessage {

	private int duration;
	private byte[] videoBlobId;
	private int videoSize;
	private byte[] thumbnailBlobId;
	private int thumbnailSize;
	private byte[] encryptionKey;

	public GroupVideoMessage() {
		super();
	}

	@Override
	public int getType() {
		return ProtocolDefines.MSGTYPE_GROUP_VIDEO;
	}

	@Override
	public boolean shouldPush() {
		return true;
	}

	@Override
	public boolean allowSendingProfile() {
		return true;
	}

	@Override
	public byte[] getBody() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			bos.write(getGroupCreator().getBytes(StandardCharsets.US_ASCII));
			bos.write(getGroupId().getGroupId());
			EndianUtils.writeSwappedShort(bos, (short)duration);
			bos.write(videoBlobId);
			EndianUtils.writeSwappedInteger(bos, videoSize);
			bos.write(thumbnailBlobId);
			EndianUtils.writeSwappedInteger(bos, thumbnailSize);
			bos.write(encryptionKey);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return bos.toByteArray();
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public byte[] getVideoBlobId() {
		return videoBlobId;
	}

	public void setVideoBlobId(byte[] videoBlobId) {
		this.videoBlobId = videoBlobId;
	}

	public int getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(int videoSize) {
		this.videoSize = videoSize;
	}

	public byte[] getThumbnailBlobId() {
		return thumbnailBlobId;
	}

	public void setThumbnailBlobId(byte[] thumbnailBlobId) {
		this.thumbnailBlobId = thumbnailBlobId;
	}

	public int getThumbnailSize() {
		return thumbnailSize;
	}

	public void setThumbnailSize(int thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	public byte[] getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(byte[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
}
