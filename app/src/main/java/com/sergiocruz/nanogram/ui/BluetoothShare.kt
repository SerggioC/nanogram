package com.sergiocruz.nanogram.ui

import android.provider.BaseColumns
import android.net.Uri

/**
 * Exposes constants used to interact with the Bluetooth Share manager's content
 * provider.
 */

class BluetoothShare private constructor() : BaseColumns {
    companion object {
        /**
         * The permission to access the Bluetooth Share Manager
         */
        val PERMISSION_ACCESS = "android.permission.ACCESS_BLUETOOTH_SHARE"

        /**
         * The content:// URI for the data table in the provider
         */
        val CONTENT_URI = Uri.parse("content://com.android.bluetooth.opp/btopp")

        /**
         * Broadcast Action: this is sent by the Bluetooth Share component to
         * transfer complete. The request detail could be retrieved by app * as _ID
         * is specified in the intent's data.
         */
        val TRANSFER_COMPLETED_ACTION = "android.btopp.intent.action.TRANSFER_COMPLETE"

        /**
         * This is sent by the Bluetooth Share component to indicate there is an
         * incoming file need user to confirm.
         */
        val INCOMING_FILE_CONFIRMATION_REQUEST_ACTION =
            "android.btopp.intent.action.INCOMING_FILE_NOTIFICATION"

        /**
         * This is sent by the Bluetooth Share component to indicate there is an
         * incoming file request timeout and need update UI.
         */
        val USER_CONFIRMATION_TIMEOUT_ACTION =
            "android.btopp.intent.action.USER_CONFIRMATION_TIMEOUT"

        /**
         * The name of the column containing the URI of the file being
         * sent/received.
         */
        val URI = "uri"

        /**
         * The name of the column containing the filename that the incoming file
         * request recommends. When possible, the Bluetooth Share manager will
         * attempt to use this filename, or a variation, as the actual name for the
         * file.
         */
        val FILENAME_HINT = "hint"

        /**
         * The name of the column containing the filename where the shared file was
         * actually stored.
         */
        val _DATA = "_data"

        /**
         * The name of the column containing the MIME type of the shared file.
         */
        val MIMETYPE = "mimetype"

        /**
         * The name of the column containing the direction (Inbound/Outbound) of the
         * transfer. See the DIRECTION_* constants for a list of legal values.
         */
        val DIRECTION = "direction"

        /**
         * The name of the column containing Bluetooth Device Address that the
         * transfer is associated with.
         */
        val DESTINATION = "destination"

        /**
         * The name of the column containing the flags that controls whether the
         * transfer is displayed by the UI. See the VISIBILITY_* constants for a
         * list of legal values.
         */
        val VISIBILITY = "visibility"

        /**
         * The name of the column containing the current user confirmation state of
         * the transfer. Applications can write to this to confirm the transfer. the
         * USER_CONFIRMATION_* constants for a list of legal values.
         */
        val USER_CONFIRMATION = "confirm"

        /**
         * The name of the column containing the current status of the transfer.
         * Applications can read this to follow the progress of each download. See
         * the STATUS_* constants for a list of legal values.
         */
        val STATUS = "status"

        /**
         * The name of the column containing the total size of the file being
         * transferred.
         */
        val TOTAL_BYTES = "total_bytes"

        /**
         * The name of the column containing the size of the part of the file that
         * has been transferred so far.
         */
        val CURRENT_BYTES = "current_bytes"

        /**
         * The name of the column containing the timestamp when the transfer is
         * initialized.
         */
        val TIMESTAMP = "timestamp"

        /**
         * This transfer is outbound, e.g. share file to other device.
         */
        val DIRECTION_OUTBOUND = 0

        /**
         * This transfer is inbound, e.g. receive file from other device.
         */
        val DIRECTION_INBOUND = 1

        /**
         * This transfer is waiting for user confirmation.
         */
        val USER_CONFIRMATION_PENDING = 0

        /**
         * This transfer is confirmed by user.
         */
        val USER_CONFIRMATION_CONFIRMED = 1

        /**
         * This transfer is auto-confirmed per previous user confirmation.
         */
        val USER_CONFIRMATION_AUTO_CONFIRMED = 2

        /**
         * This transfer is denied by user.
         */
        val USER_CONFIRMATION_DENIED = 3

        /**
         * This transfer is timeout before user action.
         */
        val USER_CONFIRMATION_TIMEOUT = 4

        /**
         * This transfer is visible and shows in the notifications while in progress
         * and after completion.
         */
        val VISIBILITY_VISIBLE = 0

        /**
         * This transfer doesn't show in the notifications.
         */
        val VISIBILITY_HIDDEN = 1

        /**
         * Returns whether the status is informational (i.e. 1xx).
         */
        fun isStatusInformational(status: Int): Boolean {
            return status >= 100 && status < 200
        }

        /**
         * Returns whether the transfer is suspended. (i.e. whether the transfer
         * won't complete without some action from outside the transfer manager).
         */
        fun isStatusSuspended(status: Int): Boolean {
            return status == STATUS_PENDING
        }

        /**
         * Returns whether the status is a success (i.e. 2xx).
         */
        fun isStatusSuccess(status: Int): Boolean {
            return status in 200..299
        }

        /**
         * Returns whether the status is an error (i.e. 4xx or 5xx).
         */
        fun isStatusError(status: Int): Boolean {
            return status in 400..599
        }

        /**
         * Returns whether the status is a client error (i.e. 4xx).
         */
        fun isStatusClientError(status: Int): Boolean {
            return status in 400..499
        }

        /**
         * Returns whether the status is a server error (i.e. 5xx).
         */
        fun isStatusServerError(status: Int): Boolean {
            return status in 500..599
        }

        /**
         * Returns whether the transfer has completed (either with success or
         * error).
         */
        fun isStatusCompleted(status: Int): Boolean {
            return status in 200..299 || status in 400..599
        }

        /**
         * This transfer hasn't stated yet
         */
        val STATUS_PENDING = 190

        /**
         * This transfer has started
         */
        val STATUS_RUNNING = 192

        /**
         * This transfer has successfully completed. Warning: there might be other
         * status values that indicate success in the future. Use isSucccess() to
         * capture the entire category.
         */
        val STATUS_SUCCESS = 200

        /**
         * This request couldn't be parsed. This is also used when processing
         * requests with unknown/unsupported URI schemes.
         */
        val STATUS_BAD_REQUEST = 400

        /**
         * This transfer is forbidden by target device.
         */
        val STATUS_FORBIDDEN = 403

        /**
         * This transfer can't be performed because the content cannot be handled.
         */
        val STATUS_NOT_ACCEPTABLE = 406

        /**
         * This transfer cannot be performed because the length cannot be determined
         * accurately. This is the code for the HTTP error "Length Required", which
         * is typically used when making requests that require a content length but
         * don't have one, and it is also used in the client when a response is
         * received whose length cannot be determined accurately (therefore making
         * it impossible to know when a transfer completes).
         */
        val STATUS_LENGTH_REQUIRED = 411

        /**
         * This transfer was interrupted and cannot be resumed. This is the code for
         * the OBEX error "Precondition Failed", and it is also used in situations
         * where the client doesn't have an ETag at all.
         */
        val STATUS_PRECONDITION_FAILED = 412

        /**
         * This transfer was canceled
         */
        val STATUS_CANCELED = 490

        /**
         * This transfer has completed with an error. Warning: there will be other
         * status values that indicate errors in the future. Use isStatusError() to
         * capture the entire category.
         */
        val STATUS_UNKNOWN_ERROR = 491

        /**
         * This transfer couldn't be completed because of a storage issue.
         * Typically, that's because the file system is missing or full.
         */
        val STATUS_FILE_ERROR = 492

        /**
         * This transfer couldn't be completed because of no sdcard.
         */
        val STATUS_ERROR_NO_SDCARD = 493

        /**
         * This transfer couldn't be completed because of sdcard full.
         */
        val STATUS_ERROR_SDCARD_FULL = 494

        /**
         * This transfer couldn't be completed because of an unspecified un-handled
         * OBEX code.
         */
        val STATUS_UNHANDLED_OBEX_CODE = 495

        /**
         * This transfer couldn't be completed because of an error receiving or
         * processing data at the OBEX level.
         */
        val STATUS_OBEX_DATA_ERROR = 496

        /**
         * This transfer couldn't be completed because of an error when establishing
         * connection.
         */
        val STATUS_CONNECTION_ERROR = 497
    }

}