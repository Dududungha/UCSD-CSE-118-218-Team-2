using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class heartratethreshold_blue1 : MonoBehaviour
{
    public ParticleSystem particleSystem;
    private int triggerThreshold = 0;
    private bool isParticlePlaying = false;
    private int currentHeartrate = 0;

    private ParticleSystem.MainModule particleMain;
    private Color originalColor;

    private bool pause = false;
    private float timer = 0f;

    public AudioSource music;
    private bool isMusicPlaying = false;

    // Start is called before the first frame update
    void Start()
    {
        particleMain = particleSystem.main;
        originalColor = particleMain.startColor.color;

        particleMain.startColor = Color.gray;

        particleSystem.Play();
    }

    // Update is called once per frame
    void Update()
    {
        if(!pause){
            timer += Time.deltaTime;

            if (timer >= 2f){
              particleSystem.Pause();
              pause = true;
            }
        }

        currentHeartrate = HeartrateEventManager.heartrate;

        if (currentHeartrate >= triggerThreshold && currentHeartrate < 80 && !isParticlePlaying)
        {
            ActivateParticleSystem();
            PlayMusic();
        }
        else if (currentHeartrate < triggerThreshold && isParticlePlaying)
        {
            DeactivateParticleSystem();
            StopMusic();
        }
        else if (currentHeartrate >= 80)
        {
            StopMusic();
        }
        else if (currentHeartrate >= triggerThreshold && currentHeartrate < 80){
            PlayMusic();
        }
    }

    private void ActivateParticleSystem()
    {
        if (!particleSystem.isPlaying)
        {
            particleMain.startColor = originalColor;

            particleSystem.Play();
            isParticlePlaying = true;
        }
    }

    private void DeactivateParticleSystem()
    {
        if (particleSystem.isPlaying)
        {
            isParticlePlaying = false;

            particleMain.startColor = Color.gray;
            particleSystem.Play();

            float timer1 = 0f;
            timer1 += Time.deltaTime;

            if (timer1 >= 2f){
              particleSystem.Pause();
            }
        }
    }

    private void PlayMusic()
    {
        if(!isMusicPlaying && music != null)
        {
          music.Play();
          isMusicPlaying = true;
        }
    }

    private void StopMusic(){
        if(isMusicPlaying && music != null)
        {
          music.Pause();
          isMusicPlaying = false;
        }
    }


}
