%% Author: Xuyi Ruan 
%% reference: 
% http://www.mathworks.com/matlabcentral/fileexchange/38837-sound-analysis-with-matlab-implementation/content/Sound_Analysis.m
% https://books.google.com/books?id=KXwAAQAAQBAJ&pg=PA11&lpg=PA11&dq=pcm+extension+matlab&source=bl&ots=o-AYfPikpI&sig=uXRjwJfsXW76KcHoJZPVeTWZaR8&hl=en&sa=X&ved=0CE4Q6AEwB2oVChMI58nT4O2EyQIVCPM-Ch2jcwlC#v=onepage&q=pcm%20extension%20matlab&f=false

clear

% read in pcm file

fid = fopen('reverseme_wave.pcm', 'r');
sound = fread(fid, inf, 'int16', 0, 'ieee-be');
fclose (fid);

sound = sound(:,1);             % get the first channel
sound_max = max(abs(sound));     % find the maximum value
sound = sound/sound_max;             % scalling the signal

% time & discretisation parameters
%N = length(sound);
N=8192;
fs = 44100;
t = (0:N-1)/fs; 

% plotting of the waveform
%figure(1)
%plot(t, sound, 'r')
%xlim([0 max(t)])
%ylim([-1.1*max(abs(sound)) 1.1*max(abs(sound))])
%grid on
%set(gca, 'FontName', 'Times New Roman', 'FontSize', 14)
%xlabel('Time, s')
%ylabel('Normalized amplitude')
%title('The signal in the time domain')


% play audio
soundsc(sound, fs);

% compute the DFT of signal 'x'
rapX = fft(sound(1:8192));
fhat = (-N/2:N/2-1)/N;
fHz = fhat*fs;

% rapXB is the signal with bass being boosted
rapXB = rapX;
for i = 1:500/fs*N
    rapXB(i) = abs(rapXB(i));
end
for j = N - round((500/fs*N)):N
     rapXB(j) = abs(rapXB(j));
end

fHz2 = fhat*fs;

figure
semilogy(fHz2, fftshift(abs(rapXB)))
xlabel('Frequency in Hz');
ylabel('Magnitude of the DFT coefficients');
set(gca, 'FontName', 'Times New Roman', 'FontSize', 14)
grid on
title('DFT Frequency Analysis');


