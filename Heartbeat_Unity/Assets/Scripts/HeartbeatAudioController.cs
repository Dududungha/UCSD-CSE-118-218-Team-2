using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartbeatAudioController : MonoBehaviour
{
    AudioSource HeartbeatAudio;
    public float IntervalTime = 4f;
    // Start is called before the first frame update
    void Start()
    {
        HeartbeatAudio = GetComponent<AudioSource>();
        HeartrateEventManager.OnHeartrateUpdate += SetHeartrateAudioBPM;
    }

    void Update() {
        IntervalTime -= Time.deltaTime;
    }

    private void SetHeartrateAudioBPM(object sender, HeartrateEventArgs e) {
        if (IntervalTime < 0) {
            IntervalTime = 4f;
            StartCoroutine(PlayHeartbeatCoroutine(e.heartrate));
        }
    }

    private IEnumerator PlayHeartbeatCoroutine(int heartrate) {
        float interval = 60f / heartrate;
        CancelInvoke("PlayHeartbeat");
        yield return new WaitForSeconds(0.5f);
        InvokeRepeating("PlayHeartbeat", 0f, interval);
    }

    void PlayHeartbeat() {
        HeartbeatAudio.PlayOneShot(HeartbeatAudio.clip);
    }
}
