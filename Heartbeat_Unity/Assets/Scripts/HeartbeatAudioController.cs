using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartbeatAudioController : MonoBehaviour
{
    AudioSource HeartbeatAudio;
    // Start is called before the first frame update
    void Start()
    {
        HeartbeatAudio = GetComponent<AudioSource>();
        HeartrateEventManager.OnHeartrateUpdate += SetHeartrateAudioBPM;
    }

    private void SetHeartrateAudioBPM(object sender, HeartrateEventArgs e) {
        float interval = 60f / e.heartrate;
        CancelInvoke("PlayHeartbeat");
        InvokeRepeating("PlayHeartbeat", 0f, interval);
    }

    void PlayHeartbeat() {
        HeartbeatAudio.PlayOneShot(HeartbeatAudio.clip);
    }
}
