using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class HeartrateEventManager : MonoBehaviour
{
    public static event EventHandler<HeartrateEventArgs> OnHeartrateUpdate;

    public static int heartrate;
    public static bool HeartrateIsUpdated = false;

    public void UpdateHeartrate(HeartrateEventArgs e) {
        if (e.heartrate != 0) {
            heartrate = e.heartrate;
            OnHeartrateUpdate?.Invoke(this, e);
        }
    }

    void Update() {
        if (HeartrateIsUpdated) {
            HeartrateEventArgs args = new HeartrateEventArgs();
            args.heartrate = heartrate;
            UpdateHeartrate(args);
            HeartrateIsUpdated = false;
        }
    }
}

public class HeartrateEventArgs : EventArgs {
    public int heartrate { get; set; }
}
