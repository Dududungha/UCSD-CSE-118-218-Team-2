using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class HeartrateEventManager
{
    public static event EventHandler<HeartrateEventArgs> OnHeartrateUpdate;

    private float heartrate;

    public void UpdateHeartrate(HeartrateEventArgs e) {
        heartrate = e.heartrate;
        OnHeartrateUpdate?.Invoke(this, e);
    }
}

public class HeartrateEventArgs : EventArgs {
    public float heartrate { get; set; }
}