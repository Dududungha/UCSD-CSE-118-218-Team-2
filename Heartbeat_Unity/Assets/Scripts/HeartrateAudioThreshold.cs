using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartrateAudioThreshold : MonoBehaviour
{
    public AudioSource audio;
    public int heartrateThreshold;

    private bool IsPlaying = false;

    // Start is called before the first frame update
    void Start()
    {
        HeartrateEventManager.OnHeartrateUpdate += PlayAudioOnHeartrateThreshold;
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private void PlayAudioOnHeartrateThreshold(object sender, HeartrateEventArgs e) {
        if (e.heartrate >= heartrateThreshold && !IsPlaying) {
            IsPlaying = true;
            audio.Play();
        }
    }
}
