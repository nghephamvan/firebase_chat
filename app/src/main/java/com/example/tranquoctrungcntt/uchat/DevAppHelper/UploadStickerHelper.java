package com.example.tranquoctrungcntt.uchat.DevAppHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.tranquoctrungcntt.uchat.Models.StickerToShow;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_STICKERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseStorageNode.STORAGE_STICKERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_STORAGE;

public class UploadStickerHelper {

    private final Context context;

    public UploadStickerHelper(Context context) {
        this.context = context;
    }


    public void uploadStickers(ArrayList<StickerToUpload> list, String stickerType, String stickerTypeDir) {

        for (StickerToUpload stickerToUpload : list) {

            Drawable drawable = context.getResources().getDrawable(stickerToUpload.getSticker());
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] bitmapdata = stream.toByteArray();


            final StorageReference reference = ROOT_STORAGE
                    .child(STORAGE_STICKERS).child(stickerTypeDir)
                    .child(stickerToUpload.getName() + ".png");

            UploadTask uploadTask = reference.putBytes(bitmapdata);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        return reference.getDownloadUrl();
                    } else {

                        throw task.getException();
                    }

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        ROOT_REF.child(CHILD_STICKERS)
                                .child(stickerType)
                                .push().setValue(new StickerToShow(stickerToUpload.getName(), task.getResult().toString()));

                    }
                }
            });

        }
    }

}
